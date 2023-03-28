package d4d.com.svd_basic_plus.oceanspace

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import d4d.com.svd_basic_plus.comunication.WebSocketComunication
import d4d.com.svd_basic_plus.proyecto.ProyectoActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


interface SpaceRegionRepresentable {
    fun endpoint(): String
}

/**
 * Represents a region in which a Digital Ocean Space can be created
 */
enum class SpaceRegion: SpaceRegionRepresentable {
    NYC {
        override fun endpoint(): String {
            return "https://nyc3.digitaloceanspaces.com/"
        }
    }, AMS {
        override fun endpoint(): String {
            return "https://nyc3.digitaloceanspaces.com/"
        }
    }, SFO {
        override fun endpoint(): String {
            return "https://nyc3.digitaloceanspaces.com/"
        }
    }
}

class SpacesFileRepository (context: Context) {
    private val accesskey = "OSSSZCC2UFFDMF7DKT7W"
    private val secretkey = "9s7qcG3XAarahbLXP806kVVC0ScSYARh1O1Gd3aybFs"
    private val spacename = "protectia"
    private val spaceregion = SpaceRegion.NYC
    private val url_space ="https://protectia.nyc3.digitaloceanspaces.com/"

    private val filename = "example_image"
    private val filetype = "jpg"

    private var transferUtility: TransferUtility
    private var appContext: Context

    init {
        val config = ClientConfiguration()
        config.socketTimeout = 0
        val credentials = StaticCredentialsProvider(BasicAWSCredentials(accesskey, secretkey))
        val client = AmazonS3Client(credentials, Region.getRegion("us-east-1"))

        client.endpoint = spaceregion.endpoint()

        transferUtility = TransferUtility.builder().s3Client(client).context(context).build()
        appContext = context
        TransferNetworkLossHandler.getInstance(appContext);
    }

    /**
     * Converts a APK resource to a file for uploading with the S3 SDK
     */
    private fun convertResourceToFile(): File {
        val exampleIdentifier = appContext.resources.getIdentifier(filename, "drawable", appContext.packageName)
        val exampleBitmap = BitmapFactory.decodeResource(appContext.resources, exampleIdentifier)

        val exampleFile = File(appContext.filesDir, Date().toString())
        exampleFile.createNewFile()

        val outputStream = ByteArrayOutputStream()
        exampleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val exampleBitmapData = outputStream.toByteArray()

        val fileOutputStream = FileOutputStream(exampleFile)
        fileOutputStream.write(exampleBitmapData)
        fileOutputStream.flush()
        fileOutputStream.close()

        return exampleFile
    }

    /**
     * Uploads the example file to a DO Space
     */
    fun  uploadExampleFile(file:File, name: String, path_nube: String, tipo_subida: String, tipo_archivo: String, etiqueta: String){

        //Starts the upload of our file
        Log.i("PATH CLOUD",path_nube)
        //var listener = transferUtility.upload(spacename, "archivos/$filename.$filetype", convertResourceToFile())
        val filePermission = CannedAccessControlList.PublicRead
        var listener = transferUtility.upload(spacename, path_nube, file, filePermission)

        //Listens to the file upload progress, or any errors that might occur
        listener.setTransferListener(object: TransferListener {
            override fun onError(id: Int, ex: Exception?) {
                Log.e("S3 UploadonError", ex.toString())
                //WebSocketComunication.confirmaSubidaArchivo(name,ex.toString())

                if(tipo_subida.equals("chat")) {
                    //WebSocketComunication.confirmaSubidaArchivo(name,ex.toString())
                }else if(tipo_subida.equals("proyecto")){
                    //String urls=url.replaceAll("\\\\/", "/");
                    //Log.d("URAL", url);
                    if (ProyectoActivity.contextProAct != null) {
                        ProyectoActivity.respondeRogelio(" :( se produjo un error, vuelva a intentarlo.")
                    }
                }

            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                Log.i("S3 Upload", "Progress ${((bytesCurrent/bytesTotal)*100)}")

            }

            override fun onStateChanged(id: Int, state: TransferState?) {
                if (state == TransferState.COMPLETED){
                    Log.i("S3 Upload", "Completed")
                    if(tipo_subida.equals("chat")) {
                        WebSocketComunication.confirmaSubidaArchivo(name, url_space + path_nube)
                    }else if(tipo_subida.equals("proyecto")){
                        WebSocketComunication.confirmaSubidaArchivoProyecto(name, url_space + path_nube, tipo_archivo, etiqueta)
                    }
                }


            }
        })
    }

    /**
     * Downloads example file from a DO Space
     */
    fun downloadExampleFile(callback: (File?, Exception?) -> Unit) {
        //Create a local File object to save the remote file to
        val file = File("${appContext.cacheDir}/$filename.$filetype")

        //Download the file from DO Space
        var listener = transferUtility.download(spacename, "$filename.$filetype", file)

        //Listen to the progress of the download, and call the callback when the download is complete
        listener.setTransferListener(object: TransferListener {
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                Log.i("S3 Download", "Progress ${((bytesCurrent/bytesTotal)*100)}")
            }

            override fun onStateChanged(id: Int, state: TransferState?) {
                if (state == TransferState.COMPLETED){
                    Log.i("S3 Download", "Completed")
                    callback(file, null)
                }
            }

            override fun onError(id: Int, ex: Exception?) {
                Log.e("S3 Download", ex.toString())
                callback(null, ex)
            }
        })
    }
}