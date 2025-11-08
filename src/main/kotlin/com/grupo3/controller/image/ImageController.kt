package com.grupo3.controller.image

import com.grupo3.dto.image.ImageUpdateResponse
import com.grupo3.service.image.ImageService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class ImageController(
    private val imageService: ImageService
) {

    @MutationMapping
    fun updateImage(
        @Argument imageId: String,
        @Argument imageUrl: String
    ): ImageUpdateResponse = imageService.updateImage(imageId, imageUrl)
}