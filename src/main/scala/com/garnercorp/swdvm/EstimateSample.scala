package com.garnercorp.swdvm

object EstimateSample {
  val Values = Vector(
    Task("Initial setup, DB, login page, etc", 25),
    Task("GPS Import from bus provider", 25),
    Task("User admin, parties, security (needed for people to see any reports)", 10),
    Task("Bus route time schedule report", 8),
    Task("Bus arrival/departure report", 8),
    Task("Passenger count import", 10),
    Task("Passenger route count report", 5),
    Task("Route utilization summary", 10),
    Task("Stop quality report", 8),
    Task("Passenger stop map", 20),
    Task("Passenger waiting time report", 10),
    Task("Transport co-ordinator mobile webapp setup", 20),
    Task("Transport co-ordinator data entry screens", 20),
    Task("Transport co-ordinator reporting", 20),
    Task("Bus provider invoice import", 10),
    Task("Bus provider invoice backup import", 10),
    Task("Bus provider tariff entry", 15),
    Task("Invoice validation report", 10),
    Task("Cost allocation report", 8),
    Task("Cost trend report", 10),
    Task("Route time trend report", 10),
    Task("Arrival departure trend report", 8),
    Task("Route time exceptions report", 10),
    Task("Unknowns", 30)
  )
}

