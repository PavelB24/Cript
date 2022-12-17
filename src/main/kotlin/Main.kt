import java.io.File
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import kotlin.system.exitProcess

class Main {

    companion object {
        private const val ALGORITHM = "AES"

        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isEmpty()) {
                println("Input arguments")
                exitProcess(-1)
            }
            if (args.size == 1) {
                if (args[0] == "-h" || args[0] == "help") {
                    println("-e : encrypt file with path (-e (path))")
                    println("-d : decrypt file with key and path (-d (key) (path))")
                    println("-ewc : encrypt file with key and path (-ewk (key) (path))")
                    exitProcess(0)
                }
            }
            when (args[0]) {
                "-e" -> {
                    if (args.size < 2) {
                        println("Input mode and file path")
                        exitProcess(-1)
                    } else encrypt(args[1])
                }

                "-d" -> {
                    if (args.size < 3) {
                        println("Input mode, key and file path")
                        exitProcess(-1)
                    } else decrypt(args[1], args[2])
                }

                "-ewk" -> {
                    if (args.size < 3) {
                        println("Input mode, key and file path")
                        exitProcess(-1)
                    } else encryptWithKey(args[1], args[2])
                }

                else -> {
                    println("Wrong arguments")
                    exitProcess(-1)
                }
            }
        }

        @JvmStatic
        private fun encryptWithKey(key: String, path: String) {
            val file = File(path)
            if (file.exists() && file.isFile) {
                try {
                    val inputData = file.readBytes()
                    val keySpec = SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM)
                    val cipher = Cipher.getInstance(ALGORITHM).also { it.init(Cipher.ENCRYPT_MODE, keySpec) }
                    val encrypted = cipher.doFinal(inputData)
                    val newFile = File(file.parent + File.separator + "encrypted")
                    newFile.apply {
                        if (exists() && isFile) {
                            delete()
                        }
                    }
                    newFile.apply {
                        createNewFile()
                        setWritable(true)
                        writeBytes(encrypted)
                    }
                    println("Encryption successful")
                    exitProcess(0)
                } catch (exc: Exception) {
                    exc.printStackTrace()
                    exitProcess(-1)
                }
            } else {
                println("File not found")
                exitProcess(-1)
            }
        }

        @JvmStatic
        fun encrypt(path: String) {
            val keygen = KeyGenerator.getInstance(ALGORITHM).also {
                it.init(256, SecureRandom())
            }
            val sks = SecretKeySpec(keygen.generateKey().encoded, ALGORITHM)
            val key = String(Base64.getEncoder().encode(sks.encoded))
            println(key)
            val file = File(path)
            if (file.exists() && file.isFile) {
                try {
                    val inputData = file.readBytes()
                    val cipher = Cipher.getInstance(ALGORITHM).also { it.init(Cipher.ENCRYPT_MODE, sks) }
//                    val keySpec = SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM)
                    val encrypted = cipher.doFinal(inputData)
                    val newFile = File(file.parent + File.separator + "encrypted")
                    if (newFile.exists() && newFile.isFile) {
                        newFile.delete()
                    }
                    newFile.apply {
                        createNewFile()
                        setWritable(true)
                        writeBytes(encrypted)
                    }
                    println("Encryption successful")
                    exitProcess(0)
                } catch (exc: Exception) {
                    exc.printStackTrace()
                    exitProcess(-1)
                }
            } else {
                println("File not found")
                exitProcess(-1)
            }
        }

        @JvmStatic
        fun decrypt(key: String, path: String) {
            try {
                val file = File(path)
                val target = File(file.parent + File.separator + "decrypted")
                if (target.exists() && target.isFile) {
                    target.delete()
                }
                target.apply {
                    createNewFile()
                    setWritable(true)
                }
                val inputData = file.readBytes()
                val keySpec = SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM)
                val cipher = Cipher.getInstance(ALGORITHM).also { it.init(Cipher.DECRYPT_MODE, keySpec) }
                val decrypted = cipher.doFinal(inputData)
                target.writeBytes(decrypted)
                println("Decryption successful")
                exitProcess(0)
            } catch (exc: Exception) {
                exc.printStackTrace()
                exitProcess(-1)
            }
        }
    }
}
