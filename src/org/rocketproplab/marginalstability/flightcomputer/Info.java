package org.rocketproplab.marginalstability.flightcomputer;

public enum Info {
  INIT_SUBSYSTEMS_START("Starting Subsystems."),
  FINISH_SUBSYSTEM_START("Subsystems started."),
  DONE_CREATING_SENSORS("Finished creating sensors.");
  
  private String description;
  
  Info(String description){
    this.description = description;
  }
  
  public String getDescription() {
    return this.description;
  }
}
