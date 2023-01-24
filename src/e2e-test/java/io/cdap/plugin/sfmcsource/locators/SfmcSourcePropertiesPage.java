/*
 * Copyright © 2022 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.plugin.sfmcsource.locators;

import io.cdap.e2e.utils.SeleniumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

/**
 * Represents Salesforce Marketing Cloud - Source plugin - Properties page - Locators.
 */
public class SfmcSourcePropertiesPage {

  @FindBy(how = How.XPATH, using = "//div[@data-cy='select-objectName']")
  public static WebElement objectDropdownForSIngleObjectMode;

  @FindBy(how = How.XPATH, using = "//div[@data-cy='multiselect-objectList']")
  public static WebElement objectDropdownForMultiObjectMode;

  @FindBy(how = How.XPATH, using = "//div[@data-cy='key']//input")
  public static WebElement dataExtensionExternalKeyInputForMultiObjectMode;

  @FindBy(how = How.XPATH, using = "//li[@data-cy='multioption-Data Extension']")
  public static WebElement selectOptionDataExtension;

  public static WebElement locateObjectCheckBoxInMultiObjectsSelector(String sobjects) {
    String xpath = "//li[@data-cy='multioption-" + sobjects + "']";
    return SeleniumDriver.getDriver().findElement(By.xpath(xpath));
  }
}
