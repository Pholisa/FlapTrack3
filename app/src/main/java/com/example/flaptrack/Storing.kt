package com.example.flaptrack

data class StoringPersonalInfo(var firstName: String? = null, var surname: String? = null, var age: String? = null )
data class Saving(var birdName: String? = null, var birdSpecie: String? = null,var date: String? = null,var dataImage : String? = null)

data class SavingData(var birdName: String? = null, var birdSpecie: String? = null,var date: String? = null,var dataImage: Int)
