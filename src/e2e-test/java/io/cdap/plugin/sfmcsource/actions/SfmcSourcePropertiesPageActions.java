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

package io.cdap.plugin.sfmcsource.actions;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.cdap.e2e.pages.locators.CdfPluginPropertiesLocators;
import io.cdap.e2e.utils.*;
import io.cdap.plugin.sfmcsource.locators.SfmcSourcePropertiesPage;
import io.cdap.plugin.tests.hooks.TestSetupHooks;
import io.cdap.plugin.utils.enums.Sobjects;
import org.junit.Assert;
import org.openqa.selenium.json.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;



/**
 * Represents - Salesforce Marketing Cloud - Source plugin - Properties page - Actions.
 */
public class SfmcSourcePropertiesPageActions {
  private static final Logger logger = LoggerFactory.getLogger(SfmcSourcePropertiesPageActions.class);

  private static Gson gson = new Gson();

  static {
    SeleniumHelper.getPropertiesLocators(SfmcSourcePropertiesPage.class);
  }

  public static void configureSourcePluginForObjectNameInSingleObjectMode(Sobjects objectName) {
    logger.info("Select dropdown option: " + objectName.value);
    ElementHelper.selectDropdownOption(SfmcSourcePropertiesPage.objectDropdownForSIngleObjectMode,
            CdfPluginPropertiesLocators.locateDropdownListItem(objectName.value));
  }

  public static void selectObjectNamesInMultiObjectMode(List<Sobjects> objectNames) {
    int totalSObjects = objectNames.size();

    SfmcSourcePropertiesPage.objectDropdownForMultiObjectMode.click();

    for (int i = 0; i < totalSObjects; i++) {
      logger.info("Select checkbox option: " + objectNames.get(i).value);
      ElementHelper.selectCheckbox(SfmcSourcePropertiesPage.
              locateObjectCheckBoxInMultiObjectsSelector(objectNames.get(i).value));
    }

    //We need to click on the Plugin Properties page header to dismiss the dialog
    ElementHelper.clickUsingActions(CdfPluginPropertiesLocators.pluginPropertiesPageHeader);
  }

  public static void fillDataExtensionExternalKey(String key) {
    ElementHelper.sendKeys(SfmcSourcePropertiesPage.dataExtensionExternalKeyInputForMultiObjectMode, key);
  }

  public static void verifyIfRecordCreatedInSinkIsCorrect(String sfmcRecordJsonArray)
          throws IOException, InterruptedException {
    JsonArray sfmcRecordArray = gson.fromJson(sfmcRecordJsonArray, JsonArray.class);
    String sfmcRecord = sfmcRecordArray.get(0).toString();
    JsonObject sfmcJsonObject = gson.fromJson(String.valueOf(sfmcRecordArray.get(0)), JsonObject.class);
    String uniqueId = sfmcJsonObject.get("eventDate").getAsString();


    TableResult bigQueryTableData = getBigQueryTableData(TestSetupHooks.bqTargetDataset, TestSetupHooks.bqTargetTable,
            uniqueId);

    if (bigQueryTableData == null) {
      return;
    }
    String bigQueryRecord = bigQueryTableData.getValues().iterator().next().get(0).getValue().toString();
    Assert.assertTrue(compareValueOfBothJson(sfmcRecord, bigQueryRecord));
  }

  private static boolean compareValueOfBothJson(String sfmcResponse, String bigQueryResponse) {
    Type type = new TypeToken<Map<String, Object>>() {
    }.getType();

    Map<String, Object> sfmcResponseInMap = gson.fromJson(sfmcResponse, type);
    Map<String, Object> bigQueryResponseInMap = gson.fromJson(bigQueryResponse, type);
    MapDifference<String, Object> mapDifference = Maps.difference(sfmcResponseInMap, bigQueryResponseInMap);

    return mapDifference.areEqual();

  }

  public static TableResult getBigQueryTableData(String dataset, String table, String uniqueId)
          throws IOException, InterruptedException {
    String projectId = PluginPropertyUtils.pluginProp("projectId");
    String selectQuery = "SELECT TO_JSON(t) result FROM `" + projectId + "." + dataset + "." + table +
            "` AS t where eventDate='" + uniqueId + "'";

    return BigQueryClient.getQueryResult(selectQuery);

  }
}
