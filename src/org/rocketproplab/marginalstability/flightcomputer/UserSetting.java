package org.rocketproplab.marginalstability.flightcomputer;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
@Documented
/**
 * An annotation to store the name and comment for a settings variable
 * @author Max Apodaca
 *
 */
public @interface UserSetting {
  /**
   * The name to use for the settings config file. If "" then the field name should be used.
   * @return the name to use for the settings file, "" if field name should be used
   */
  String name() default "";
  
  /**
   * The comment to display in the settings config file. "" will not be written to the file.
   * @return the comment for the settings file
   */
  String comment();
  
  /**
   * The units to display in the settings file. "" will not be written to the file.
   * @return the units for the settings file
   */
  String units();
}
