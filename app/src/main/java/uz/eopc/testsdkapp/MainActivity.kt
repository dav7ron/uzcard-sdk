package uz.eopc.testsdkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import uz.MainProcessorImpl
import uz.MainProcessorErrorListener
import uz.ProviderDataLoader
import uz.StateStatus
import uz.eopc.testsdkapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dataLoader: ProviderDataLoader


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataLoader = ProviderDataLoader(this)

        val mainProcessor = MainProcessorImpl(this, dataLoader)

        // Initially hide the button
        binding.btnStartTokenization.visibility = View.GONE
        binding.tvStatus.visibility = View.GONE

        mainProcessor.checkStatus(
            "c5165a4a18a41517",
            "+998977771357",
            "561468******6932",
            object : MainProcessorErrorListener {
                override fun onErrorListener(status: StateStatus) {
                    Toast.makeText(
                        this@MainActivity,
                        status.toString(),
                        Toast.LENGTH_LONG,
                    ).show()

                    if (status == StateStatus.CARD_NOT_TOKENIZED || status == StateStatus.USER_NOT_REGISTERED) {
                        binding.btnStartTokenization.visibility = View.VISIBLE
                    } else if (status == StateStatus.CARD_TOKENIZED) {
                        binding.tvStatus.visibility = View.VISIBLE
                    }
                }
            }
        )

        binding.btnStartTokenization.setOnClickListener {
            mainProcessor.startProcess(
                "e0b1cb71-3684-4762-8523-ded4392721e8",
                "c5165a4a18a41517",
                "+998977771357",
                "561468******6932",
                object : MainProcessorErrorListener {
                    override fun onErrorListener(status: StateStatus) {
                        Toast.makeText(
                            this@MainActivity,
                            status.toString(),
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            )
        }

    }
}
