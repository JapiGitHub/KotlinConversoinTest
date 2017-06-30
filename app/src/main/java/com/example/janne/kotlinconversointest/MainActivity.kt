package com.example.janne.kotlinconversointest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import java.io.IOException


class MainActivity : AppCompatActivity() {

    private var mRecordButton: RecordButton? = null
    private var mRecorder: MediaRecorder? = null

    private var mPlayButton: PlayButton? = null
    private var mPlayer: MediaPlayer? = null

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
        if (!permissionToRecordAccepted) finish()

    }

    private fun onRecord(start: Boolean) {
        if (start) {
            startRecording()
        } else {
            stopRecording()
        }
    }

    private fun onPlay(start: Boolean) {
        if (start) {
            startPlaying()
        } else {
            stopPlaying()
        }
    }

    private fun startPlaying() {
        mPlayer = MediaPlayer()
        try {
            mPlayer!!.setDataSource(mFileName)
            mPlayer!!.prepare()
            mPlayer!!.start()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }
    }

    private fun stopPlaying() {
        mPlayer!!.release()
        mPlayer = null
    }

    private fun startRecording() {


        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        /*

        Adaptive Multi-Rate (AMR) is an audio data compression scheme optimized for speech coding. AMR was adopted as the standard speech codec by 3GPP in October 1998 and is now widely used in GSM and UMTS.
        It uses link adaptation to select from one of eight different bit rates based on link conditions.

        // https://developer.android.com/reference/android/media/MediaRecorder.OutputFormat.html

        AMR NB narrow band
        AMR WB wide band

        MediaRecorder.OutputFormat.THREE_GPP);
        MediaRecorder.AudioEncoder.AMR_NB);

         */

        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)                // https://developer.android.com/reference/android/media/MediaRecorder.OutputFormat.html      MediaRecorder.OutputFormat.AMR_NB);
        mRecorder!!.setOutputFile(mFileName)
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)


        // exception handler
        try {
            mRecorder!!.prepare()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }

        mRecorder!!.start()
    }

    private fun stopRecording() {


        mRecorder!!.stop()
        mRecorder!!.release()


        println("KONSOLI  : alkamassa maxAmplitude")
        // jäit tähän torstaina 30.6.2017 crashaa jostain syystä. jos tämä on start playingi() niin sitten NULL
        var maksimiAani : Int? = mRecorder?.maxAmplitude
        println("KONSOLI  $maksimiAani")



        mRecorder = null

    }

    internal inner class RecordButton(ctx: Context) : Button(ctx) {
        var mStartRecording = true

        var clicker: View.OnClickListener = View.OnClickListener {
            onRecord(mStartRecording)
            if (mStartRecording) {
                text = "Stop recording"
            } else {
                text = "Start recording"
            }
            mStartRecording = !mStartRecording
        }

        init {
            text = "Start recording"
            setOnClickListener(clicker)
        }
    }

    internal inner class PlayButton(ctx: Context) : Button(ctx) {
        var mStartPlaying = true

        var clicker: View.OnClickListener = View.OnClickListener {
            onPlay(mStartPlaying)
            if (mStartPlaying) {
                text = "Stop playing"
            } else {
                text = "Start playing"
            }
            mStartPlaying = !mStartPlaying
        }

        init {            // ctx = context
            text = "Start playing"
            setOnClickListener(clicker)
        }// super() calls the parent's class constructor (all the way back to Object) and it runs before the current class's constructor.
    }


    public override fun onCreate(savedInstanceState: Bundle?) {       //savedInstanceState = icicle ?
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Record to the external cache directory for visibility
        mFileName = externalCacheDir.absolutePath
        mFileName += "/audiorecordtest.3gp"

        //permissions
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)


        //tekee napit
        val ll = LinearLayout(this)
        mRecordButton = RecordButton(this)
        ll.addView(mRecordButton,
                LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0f))
        mPlayButton = PlayButton(this)
        ll.addView(mPlayButton,
                LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0f))

        //tekee konsolin
        var konsoli = TextView(this)
        konsoli.text = "timer v 0.22"
        ll.addView(konsoli, 0)

        setContentView(ll)

        println("KONSOLI  TESTI  ... ")             // tämä tulee näkyviin android monitoriin :      06-30 12:05:41.012 12677-12677/? I/System.out: KONSOLI  TESTI  ...

    }


    public override fun onStop() {
        super.onStop()
        if (mRecorder != null) {
            mRecorder!!.release()
            mRecorder = null
        }

        if (mPlayer != null) {
            mPlayer!!.release()
            mPlayer = null
        }
    }

    companion object {

        private val LOG_TAG = "AudioRecordTest"
        private val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private var mFileName: String? = null
    }


}
