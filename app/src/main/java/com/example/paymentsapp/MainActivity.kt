package com.example.paymentsapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import androidx.compose.ui.window.Dialog
import com.example.paymentsapp.ui.theme.PaymentsAppTheme
import com.example.paymentsapp.ui.theme.PrimaryGreen
import dev.shreyaspatil.easyupipayment.EasyUpiPayment
import dev.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import dev.shreyaspatil.easyupipayment.model.PaymentApp
import dev.shreyaspatil.easyupipayment.model.TransactionDetails
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity(), PaymentStatusListener {

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaymentsAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                backgroundColor = PrimaryGreen,
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
                        Payments(this)
                    }
                }
            }
        }
    }

    override fun onTransactionCancelled() {
        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_SHORT).show()
    }

    override fun onTransactionCompleted(transactionDetails: TransactionDetails) {
        val builder = AlertDialog.Builder(this)
        if(transactionDetails.transactionStatus.toString() == "FAILURE") {
            builder.setTitle("Transaction Failed!")
            val arr = arrayOf("Transaction ID : ${transactionDetails.transactionId}",
                "Amount : Rs. ${transactionDetails.amount}",
                "Transaction Status: ${transactionDetails.transactionStatus}",
                "Transaction Ref ID: ${transactionDetails.transactionRefId}",
                )
            builder.setItems(arr) {
                _ , which ->
                    when(which) {
                        0 -> {}
                        1 -> {}
                        2 -> {}
                    }
            }
            val dialog = builder.create()
            dialog.show()
            Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show()
        }else if(transactionDetails.transactionStatus.toString() == "SUCCESS"){
            Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show()
        }
//        if(transactionDetails.transactionStatus.equals(false)){
//            AlertComponent()
//        }

    }


}

//@Composable
//fun AlertComponent(){
//    var ctx = LocalContext.current
//    AlertDialog(onDismissRequest = {},
//        title = Text(text = "ANDROID UPI PAYMENT", color = Color.White),
//    text = Text("Transaction failed", color = Color.White),
//        backgroundColor = Color.Red,
//        dismissButton = TextButton(onClick = {showDialog.value = false}) {
//            Text(text = "Dismiss", color = Color.White)
//        }
//    )
//}

//@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
//@Composable
//fun App(){
//    var ctx = LocalContext.current
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                backgroundColor = Color.Gray,
//                title = {
//                    Text(
//                        text = "ANDROID UPI PAYMENT", textAlign = TextAlign.Center,
//                        modifier = Modifier.fillMaxWidth(),
//                        color = Color.White
//                    )
//                }
//            )
//        }
//    ) {
//        val activity = LocalContext.current as? Activity
//        Payments(activity)
//    }
//}


@Composable
fun Payments(mainActivity: MainActivity) {
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
            Image(painter = painterResource(id = R.drawable.payment_img), alignment = Alignment.Center,contentDescription = null)
        
            Spacer(modifier = Modifier.height(25.dp))
        
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
        }, colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryGreen)) {
            Text(text = "PROCEED TO PAY", color = Color.White)
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
        println(transcId)
        val payment = EasyUpiPayment(activity) {
            this.paymentApp = PaymentApp.ALL
            this.payeeVpa = upi
            this.payeeName = name
            this.transactionId = transcId
            this.transactionRefId = transcId
            this.description = desc
            this.payeeMerchantCode = "1234"
            this.amount = amount
        }

        payment.setPaymentStatusListener(mainActivity)
        payment.startPayment()
    } catch (e: Exception) {
        e.printStackTrace()
        println(e.message)
        Toast.makeText(ctx,"Error occurred",Toast.LENGTH_SHORT).show()
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val ctx = LocalContext.current
    val activity = ctx as? Activity
    PaymentsAppTheme {
        Payments(activity as MainActivity)
    }
}