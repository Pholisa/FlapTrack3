package com.example.flaptrack

class BirdInfo{
    var birdName: String? = null
    var birdSpecies: String? = null
    var date: String? = null
    var image: String? = null

constructor(birdName: String?, birdSpecies: String?, date: String?, image: String?)
{
    this.birdName = birdName
    this.birdSpecies = birdSpecies
    this.date = date
    this.image = image
}
constructor()
{}
}
