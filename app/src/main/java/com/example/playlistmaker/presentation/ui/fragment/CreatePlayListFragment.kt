package com.example.playlistmaker.presentation.ui.fragment

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlayListBinding
import com.example.playlistmaker.presentation.util.DpToPx.toPx
import com.example.playlistmaker.presentation.viewmodel.playlist.CreatePlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class CreatePlayListFragment : Fragment() {

    private val args: CreatePlayListFragmentArgs by navArgs()

    private var isEditMode = false
    private var currentPlaylistId: Long = -1

    private var _binding: FragmentCreatePlayListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreatePlaylistViewModel by viewModel()

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            lifecycleScope.launch {
                viewModel.saveCoverImage(requireContext(), it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.playlistId != -1L) {
            isEditMode = true
            currentPlaylistId = args.playlistId
            setupEditMode(args.playlistId)
        } else {
            isEditMode = false
        }

        setupTextWatchers()
        setupClickListeners()
        setupObservers()
        setupBackPressHandler()
    }

    private fun setupEditMode(playlistId: Long) {
        binding.createButton.text = "Сохранить"

        viewModel.loadPlaylistForEditing(playlistId)

        viewModel.playlistForEditing.observe(viewLifecycleOwner) { playlist ->
            playlist?.let {
                binding.textInputEditName.setText(it.name)
                binding.textInputEditDescription.setText(it.description)
                viewModel.updateCoverImagePath(it.coverImagePath)
                loadImage(it.coverImagePath)
                viewModel.playlistName = it.name
                viewModel.playlistDescription = it.description
            }
        }
    }

    private fun setupTextWatchers() {
        binding.textInputEditName.addTextChangedListener {
            binding.createButton.isEnabled = !it.isNullOrBlank()
            viewModel.playlistName = it?.toString() ?: ""
        }
        binding.textInputEditDescription.addTextChangedListener {
            viewModel.playlistDescription = it?.toString()
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            if (isEditMode) {
                closeFragment()
            } else {
                checkForUnsavedChanges()
            }
        }

        binding.createButton.setOnClickListener {
            val playlistName = binding.textInputEditName.text?.toString() ?: ""
            if (isEditMode) {
                viewModel.updatePlaylist(
                    playlistId = currentPlaylistId,
                    name = playlistName,
                    description = binding.textInputEditDescription.text?.toString(),
                    coverImagePath = viewModel.coverImagePath.value
                )
            } else {
                viewModel.createPlaylist()
                showPlaylistCreatedToast(playlistName)
            }
        }

        binding.coverImageView.setOnClickListener {
            openImagePicker()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun setupObservers() {
        viewModel.coverImagePath.observe(viewLifecycleOwner) { path ->
            if (path.isNullOrBlank()) {
                binding.coverImageView.setImageResource(R.drawable.placeholder_image)
            } else {
                loadImage(path)
            }
        }

        viewModel.playlistCreated.observe(viewLifecycleOwner) { id ->
            id?.let {
                closeFragment()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.errorShown()
            }
        }

        viewModel.showExitDialog.observe(viewLifecycleOwner) { show ->
            if (show == true) showDiscardChangesDialog()
        }
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            checkForUnsavedChanges()
        }
    }

    private fun closeFragment() {
        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as? NavHostFragment

        if (navHostFragment?.navController?.currentDestination?.id == R.id.createPlaylistFragment) {
            navHostFragment.navController.popBackStack()
        } else {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()

            (requireActivity().findViewById<FrameLayout>(R.id.fragment_container)).visibility = View.GONE
            (requireActivity().findViewById<ConstraintLayout>(R.id.main_container)).visibility = View.VISIBLE
            (requireActivity().findViewById<LinearLayout>(R.id.playlists_bottom_sheet)).visibility = View.VISIBLE
        }
    }



    private fun openImagePicker() {
        imagePicker.launch("image/*")
    }

    private fun loadImage(path: String?) {
        if (path.isNullOrBlank()) {
            binding.coverImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            binding.coverImageView.setImageResource(R.drawable.ic_add_img)
        } else {
            binding.coverImageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(this)
                .load(File(path))
                .placeholder(R.drawable.ic_add_img)
                .transform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCorners(8.toPx())
                    )
                )
                .into(binding.coverImageView)
        }

    }

    fun showPlaylistCreatedToast(name: String) {
        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.toast_playlist_created, null)
        val toastText = layout.findViewById<TextView>(R.id.toast_text)
        toastText.text = "Плейлист $name создан!"

        val toast = Toast(requireContext())
        toast.view = layout
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 100)
        toast.show()
    }

    private fun checkForUnsavedChanges() {
        if (viewModel.hasUnsavedChanges()) {
            viewModel.onBackPressed()
        } else {
            closeFragment()
        }
    }

    private fun showDiscardChangesDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.discard_changes_title))
            .setMessage(getString(R.string.discard_changes_message))
            .setPositiveButton(getString(R.string.discard)) { _, _ ->
                viewModel.resetExitDialog()
                closeFragment()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                viewModel.resetExitDialog()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}