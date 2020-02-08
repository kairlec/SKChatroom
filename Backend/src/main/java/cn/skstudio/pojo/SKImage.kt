package cn.skstudio.pojo

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.io.OutputStream
import javax.imageio.ImageIO

class SKImage {
    var bufferedImage: BufferedImage

    constructor(bufferedImage: BufferedImage) {
        this.bufferedImage = bufferedImage
    }

    constructor(file: File) {
        bufferedImage = ImageIO.read(file)
    }

    constructor(path: String) {
        bufferedImage = ImageIO.read(File(path))
    }

    @Throws(IOException::class)
    fun write(outputStream: OutputStream) {
        ImageIO.write(bufferedImage, "jpg", outputStream)
    }

    @Throws(IOException::class)
    fun write(outputStream: OutputStream, Transparent: Boolean) {
        if (Transparent) {
            ImageIO.write(bufferedImage, "png", outputStream)
        } else {
            ImageIO.write(bufferedImage, "jpg", outputStream)
        }
    }

    @Throws(IOException::class)
    fun read(file: File) {
        bufferedImage = ImageIO.read(file)
    }
}