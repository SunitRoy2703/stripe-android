package com.stripe.example.activity

import com.stripe.example.Settings.publishableKey
import com.stripe.android.view.ActivityStarter.startForResult
import com.stripe.android.view.AddPaymentMethodActivityStarter.Args.Builder.setPaymentMethodType
import com.stripe.android.view.AddPaymentMethodActivityStarter.Args.Builder.build
import com.stripe.android.view.AddPaymentMethodActivityStarter.Result.Success.paymentMethod
import com.stripe.android.view.FpxBank.brandIconResId
import com.stripe.android.view.FpxBank.displayName
import android.os.Bundle
import com.stripe.example.R
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.stripe.android.model.PaymentMethod
import com.stripe.android.view.AddPaymentMethodActivityStarter
import com.stripe.android.view.FpxBank
import com.stripe.example.Settings
import com.stripe.example.databinding.BankSelectorPaymentActivityBinding
import java.util.Objects

class FpxPaymentActivity : AppCompatActivity() {
    private var viewBinding: BankSelectorPaymentActivityBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = BankSelectorPaymentActivityBinding.inflate(
            layoutInflater
        )
        setContentView(viewBinding!!.root)
        setTitle(R.string.fpx_payment_example)
        init.init(
            this,
            Settings(this).publishableKey
        )
        viewBinding!!.selectPaymentMethodButton
            .setOnClickListener { view: View? -> launchAddPaymentMethod() }
    }

    private fun launchAddPaymentMethod() {
        AddPaymentMethodActivityStarter(this)
            .startForResult(
                Builder()
                    .setPaymentMethodType(PaymentMethod.Type.Fpx)
                    .build()
            )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: Result = fromIntent.fromIntent(data)
        if (result is Success) {
            val successResult: Success = result as Success
            onPaymentMethodResult(successResult.paymentMethod)
        }
    }

    private fun onPaymentMethodResult(paymentMethod: PaymentMethod) {
        val fpxBankCode = Objects.requireNonNull(paymentMethod.fpx).bank
        val resultMessage = """
            Created Payment Method
            
            Type: ${paymentMethod.type}
            Id: ${paymentMethod.id}
            Bank code: $fpxBankCode
            """.trimIndent()
        viewBinding!!.paymentMethodResult.text = resultMessage
        val fpxBank: FpxBank = get.get(fpxBankCode)
        if (fpxBank != null) {
            viewBinding!!.bankInfo.visibility = View.VISIBLE
            viewBinding!!.bankInfo.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(this, fpxBank.brandIconResId!!),
                null,
                null,
                null
            )
            viewBinding!!.bankInfo.text = fpxBank.displayName
        } else {
            viewBinding!!.bankInfo.visibility = View.GONE
        }
    }
}