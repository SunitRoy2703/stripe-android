package com.stripe.android.ui.core.elements

import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class SimpleTextElement(
    override val identifier: IdentifierSpec,
    override val controller: TextFieldController
) : SectionSingleFieldElement(identifier)
