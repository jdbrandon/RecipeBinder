package com.jeffbrandon.recipebinder.fragments

import androidx.fragment.app.Fragment

abstract class EditFragment : Fragment, Saveable {
    constructor() : super()
    constructor(id: Int) : super(id)
}
