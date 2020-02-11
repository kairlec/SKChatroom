package cn.skstudio.pojo

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import javax.imageio.stream.ImageInputStream
import javax.swing.text.html.HTML.Tag.I

class SKImage {
    var bufferedImage: BufferedImage
    var contentType: String

    constructor(bufferedImage: BufferedImage, contentType: String) {
        this.bufferedImage = bufferedImage
        this.contentType = contentType
    }

    constructor(file: File) {
        bufferedImage = ImageIO.read(file)
        contentType = Files.probeContentType(file.toPath())
    }

    constructor(path: Path) {
        bufferedImage = ImageIO.read(path.toFile())
        contentType = Files.probeContentType(path)
    }

    fun write(outputStream: OutputStream) {
        ImageIO.write(bufferedImage, "jpg", outputStream)
    }

    fun write(outputStream: OutputStream, Transparent: Boolean) {
        if (Transparent) {
            ImageIO.write(bufferedImage, "png", outputStream)
        } else {
            ImageIO.write(bufferedImage, "jpg", outputStream)
        }
    }

    fun read(file: File) {
        bufferedImage = ImageIO.read(file)
    }

    companion object {
        fun isImage(file: File): Boolean {
            if (!ImageIO.getImageReaders(ImageIO.createImageInputStream(file)).hasNext()) {
                return false
            }
            ImageIO.read(file) ?: return false
            return true
        }
    }
}