package com.androiddevs.runningappyt.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.runningappyt.R
import com.androiddevs.runningappyt.adapters.ExerciseStatusAdapter
import com.androiddevs.runningappyt.db.ExerciseModel
import com.androiddevs.runningappyt.other.Constants.defaultExerciesList
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_exercise.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ExerciseFragment : Fragment(R.layout.fragment_exercise),  TextToSpeech.OnInitListener
{

    private var restTimer:CountDownTimer?=null
    private var restProgress=0

    private var exerciesTimer:CountDownTimer?=null
    private var exerciesProgress = 0

    private var exerciseList:ArrayList<ExerciseModel>? = null
    private var currentExercisePosition=-1 //later it will start from 0 when we increment it

    private var tts: TextToSpeech?=null   //variable for text to speech

    private var player: MediaPlayer?=null

    private var exerciseAdapter : ExerciseStatusAdapter?=null

//////////////////////
    private var menu: Menu? = null


    ////////////////////////
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initialize the text to speech
        tts=TextToSpeech(view.context,this)



        exerciseList=defaultExerciesList()
        setupRestView()

        setUpRecyclerView()

    }

//////////////////////////////
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }
/////////////
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

            this.menu?.getItem(0)?.isVisible = true

    }

//////////
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }
//////////////////////
    private fun showCancelTrackingDialog() {
        CancelTrackingDialog().apply {
            setYesListener {


                requireActivity().onBackPressed()
            }
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)
    }

    override fun onDestroy() {

        if(restTimer!=null)
        {
            restTimer!!.cancel()
            restProgress=0
        }

        if(exerciesTimer!=null)
        {
            exerciesTimer!!.cancel()
            exerciesProgress=0

        }

        if(tts!=null)
        {
            tts!!.stop()
            tts!!.shutdown()

        }

        if(player!=null)
        {

            player!!.stop()
        }
        super.onDestroy()
    }


    private fun setRestProgressBar()
    {
        progressBar.progress=restProgress
        restTimer = object : CountDownTimer(10000,1000)
        {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                progressBar.progress=10-restProgress
                tvTimer.text=(10-restProgress).toString()

            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()


                setupExerciseView()

            }


        }.start()








    }




    private fun setupRestView()
    {

        try {

            //when exercise finish play the sound for the user
            player=MediaPlayer.create(context, R.raw.sound)// soundURI mean the location where you can find audio
            player!!.isLooping=false //make sure player sounds only once
            player!!.start()

        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }



        llRestView.visibility = View.VISIBLE
        llExerciesView.visibility=View.GONE

        if(restTimer!=null)
        {
            restTimer!!.cancel()
            restProgress=0
        }
        tvUpcomingActivity.text=exerciseList!![currentExercisePosition+1].getName()
        setRestProgressBar()

    }



    private fun setExerciseProgressBar()
    {

        progressBarExercie.progress=exerciesProgress
        exerciesTimer = object : CountDownTimer(30000,1000)
        {
            override fun onTick(millisUntilFinished: Long) {
                exerciesProgress++
                progressBarExercie.progress=30-exerciesProgress
                tvTimerExercise.text=(30-exerciesProgress).toString()

            }

            override fun onFinish() {

                if(currentExercisePosition < exerciseList?.size!!-1)
                {
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()

                    setupRestView()
                }

                else
                {
                    findNavController().navigate(R.id.action_exerciseFragment_to_finishFragment)
                }
            }


        }.start()


    }


    private fun setupExerciseView()
    {

        llRestView.visibility = View.GONE
        llExerciesView.visibility=View.VISIBLE

        if(exerciesTimer!=null)
        {
            exerciesTimer!!.cancel()
            exerciesProgress=0
        }
        SpeakOut(exerciseList!![currentExercisePosition].getName())
        setExerciseProgressBar()

        ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        exercise_name.text=exerciseList!![currentExercisePosition].getName()

    }


    private fun SpeakOut(text: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"");
        } else {
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    override fun onInit(status: Int) //need to implemented for the Text to speech interface that we have added before
    {
        if(status==TextToSpeech.SUCCESS)
        {
            //set US English language for ttts
            var result=tts!!.setLanguage(Locale.US)


            if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED)
            {

                Log.e("TTS","The language is not supported")
                view?.let { Snackbar.make(it,"The language is not supported", Snackbar.LENGTH_SHORT).show() }
            }

        }
        else
        {


            Log.e("TTS","Initialization failed")
            view?.let { Snackbar.make(it,"Initialization failed", Snackbar.LENGTH_SHORT).show() }
        }

    }

    private fun setUpRecyclerView()
    {


        //set the LayoutManager that this recyclerview will use
        rvExerciseStatus.layoutManager= LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL,false)

        //Adapter class is initialized and list is passed in the param
        exerciseAdapter =  ExerciseStatusAdapter(exerciseList!!)

        //adapter instance is set to the recyclerview to inflate the items
        rvExerciseStatus.adapter=exerciseAdapter


    }









}