package com.example.paymentsapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paymentsapp.ui.theme.PaymentsAppTheme
import dev.shreyaspatil.easyupipayment.EasyUpiPayment
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import dev.shreyaspatil.easyupipayment.model.PaymentApp
import dev.shreyaspatil.easyupipayment.model.TransactionDetails
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity(), PaymentStatusListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaymentsAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    App()
                }
            }
        }
    }

    override fun onTransactionCancelled() {
        println("Cancelled.......")
        Toast.makeText(this, "Transaction cancelled by user..", Toast.LENGTH_LONG).show()
    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetails) {

        Toast.makeText(this, "Transaction completed by user..", Toast.LENGTH_LONG).show()
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun App(){
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.Gray,
                title = {
                    Text(
                        text = "ANDROID UPI PAYMENT", textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White
                    )
                }
            )
        }
    ) {
        payments(mainActivity = MainActivity())
    }

}



@Composable
fun payments(mainActivity: MainActivity){
    val ctx= LocalContext.current
    val activity = (LocalContext.current as? Activity)

    val amount = remember {
        mutableStateOf(TextFieldValue())
    }

    val upiId = remember {
        mutableStateOf(TextFieldValue())
    }

    val name = remember {
        mutableStateOf(TextFieldValue())
    }

    val description = remember {
        mutableStateOf(TextFieldValue())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            TextField(value = amount.value,  onValueChange = {
                amount.value = it
            }, placeholder = { Text(text = "Enter the amount") },
            textStyle = TextStyle(fontSize = 20.sp)
                )
        
        Spacer(modifier = Modifier.height(15.dp))

        TextField(value = upiId.value, onValueChange = {
            upiId.value = it
        }, placeholder = { Text(text = "Enter UPI ID") },
            textStyle = TextStyle(fontSize = 20.sp)
            )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(value = name.value, onValueChange = {
            name.value = it
        }, placeholder = { Text(text = "Enter the name of the recipient") },
            textStyle = TextStyle(fontSize = 20.sp)
            )

        Spacer(modifier = Modifier.height(15.dp))

        TextField(value = description.value, onValueChange = {
            description.value = it
        }, placeholder = { Text(text = "Enter the description") },
            textStyle = TextStyle(fontSize = 20.sp)
            )

        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            val c : Date = Calendar.getInstance().time
            val df = SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault())
            val transcId : String = df.format(c)
            println(amount.value.text)
            makePayment(
                amount.value.text,
                upiId.value.text,
                name.value.text,
                description.value.text,
                transcId,
                ctx,
                activity!!,
                mainActivity
            )
        }) {
            Text(text = "PROCEED TO PAY")
        }
    }

}

private fun makePayment(
    amount: String,
    upi: String,
    name: String,
    desc: String,
    transcId : String,
    ctx: Context,
    activity: Activity,
    mainActivity: PaymentStatusListener
) {
    try {
        val payment = EasyUpiPayment(activity) {
            this.paymentApp = PaymentApp.ALL
            this.payeeVpa = upi
            this.payeeName = name
            this.transactionId = transcId
            this.transactionRefId = transcId
            this.payeeMerchantCode = transcId
            this.description = desc
            this.amount = amount
        }

        payment.setPaymentStatusListener(mainActivity)
        payment.startPayment()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(ctx,e.message,Toast.LENGTH_SHORT).show()
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PaymentsAppTheme {
        App()
    }
}