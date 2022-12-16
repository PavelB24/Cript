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
            if (args.size == 1) {
                if (args[0] == "-h" || args[0] == "help") {
                    println("-e : encrypt file with path (-e (Path))")
                    println("-d : crypt file with key and path (-d (String) (String))")
                    exitProcess(0)
                }
            }
            if (args.isEmpty()) {
                println("Input arguments")
                exitProcess(-1)
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

                else -> {
                    println("Wrong arguments")
                    exitProcess(-1)
                }
            }
        }

        @JvmStatic
        fun encrypt(path: String) {
            val secureRandom = SecureRandom()
            val keygen = KeyGenerator.getInstance(ALGORITHM).also {
                it.init(256, secureRandom)
            }
            val sks = SecretKeySpec(keygen.generateKey().encoded, ALGORITHM)
            val key = String(Base64.getEncoder().encode(sks.encoded))
            println(key)
            val file = File(path)
            if (file.exists() && file.isFile) {
                try {
                    val inputData = file.readBytes()
                    val cipher = Cipher.getInstance(ALGORITHM)
                    val keySpec = SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM)
                    cipher.init(Cipher.ENCRYPT_MODE, keySpec)
                    val encrypted = cipher.doFinal(inputData)
                    val newFile = File(file.parent + File.separator + "encrypted")
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
                target.apply {
                    createNewFile()
                    setWritable(true)
                }
                val inputData = file.readBytes()
                val cipher = Cipher.getInstance(ALGORITHM)
                val keySpec = SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM)
                cipher.init(Cipher.DECRYPT_MODE, keySpec)
                val encrypted = cipher.doFinal(inputData)
                target.writeBytes(encrypted)
                println("Decryption successful")
                exitProcess(0)
            } catch (exc: Exception) {
                exc.printStackTrace()
                exitProcess(-1)
            }
        }
    }
}
