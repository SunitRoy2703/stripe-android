package com.stripe.android.paymentsheet.forms

import com.stripe.android.paymentsheet.paymentdatacollection.FormFragmentArguments
import com.stripe.android.paymentsheet.paymentdatacollection.getInitialValuesMap
import com.stripe.android.ui.core.elements.FormItemSpec
import com.stripe.android.ui.core.forms.TransformSpecToElements
import com.stripe.android.ui.core.forms.resources.ResourceRepository
import javax.inject.Inject

/**
 * Wrapper around [TransformSpecToElements] that uses the parameters from [FormFragmentArguments].
 */
internal class TransformSpecToElement @Inject constructor(
    resourceRepository: ResourceRepository,
    formFragmentArguments: FormFragmentArguments
) {
    private val transformSpecToElements =
        TransformSpecToElements(
            resourceRepository = resourceRepository,
            initialValues = formFragmentArguments.getInitialValuesMap(),
            amount = formFragmentArguments.amount,
            country = formFragmentArguments.billingDetails?.address?.country,
            saveForFutureUseInitialValue = formFragmentArguments.showCheckboxControlledFields,
            merchantName = formFragmentArguments.merchantName
        )

    internal fun transform(list: List<FormItemSpec>) = transformSpecToElements.transform(list)
}
