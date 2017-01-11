/*******************************************************************************
 * Copyright (c) 2013, 2016 Lukas Balzer, Asim Abdulkhaleq, Stefan Wagner
 * Institute of Software Technology, Software Engineering Group
 * University of Stuttgart, Germany
 *  
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package xstampp.astpa.model.extendedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.eclipse.core.runtime.Assert;

import xstampp.astpa.model.extendedData.interfaces.IExtendedDataController;
import xstampp.astpa.model.interfaces.IExtendedDataModel;
import xstampp.astpa.model.interfaces.IExtendedDataModel.ScenarioType;
import xstampp.model.AbstractLtlProvider;
import xstampp.model.AbstractLtlProviderData;
import xstampp.model.IEntryFilter;
import xstampp.model.IValueCombie;

public class ExtendedDataController implements IExtendedDataController {
    @XmlElementWrapper(name = "rules")
    @XmlElement(name = "rule")
    private List<AbstractLtlProvider> rules;
    private Map<UUID,AbstractLtlProvider> ruleMap;
    
    @XmlElementWrapper(name = "scenarios")
    @XmlElement(name = "scenario")
    private List<AbstractLtlProvider> scenarios;
    private Map<UUID,AbstractLtlProvider> scenarioMap;
    

    @XmlElementWrapper(name = "customLTLs")
    @XmlElement(name = "customLTL")
    private List<AbstractLtlProvider> customLTLs;
    private Map<UUID,AbstractLtlProvider> ltlMap;
    
    
    private int ruleIndex;
    
    public ExtendedDataController() {
      ruleIndex = 0;
    }
    
    private boolean validateType(String type){
        switch(type){
          case IValueCombie.TYPE_ANYTIME:
          case IValueCombie.TYPE_NOT_PROVIDED:
          case IValueCombie.TYPE_TOO_EARLY:
          case IValueCombie.TYPE_TOO_LATE:
            return true;
          default: 
            return false;
        }
      
    }
    private UUID addRuleEntry(Map<UUID,AbstractLtlProvider> entryMap,AbstractLtlProviderData data,UUID linkedControlActionID, String type){
      if(entryMap != null && data != null && validateType(type)){
        
        RefinedSafetyRule safetyRule = new RefinedSafetyRule(data,linkedControlActionID,type, ruleIndex);
        ruleIndex++;
        entryMap.put(safetyRule.getId(),safetyRule);
        return safetyRule.getRuleId();
        
      }
      return null;
    }

    private Map<UUID,AbstractLtlProvider> getMap(ScenarioType type){
      Assert.isNotNull(type);
        switch(type){
        case CUSTOM_LTL:
          if(ltlMap == null){
            ltlMap = new HashMap<>();
            fillMap(ltlMap, customLTLs);
          }
          return ltlMap;
        case BASIC_SCENARIO:
          if(ruleMap == null){
            ruleMap = new HashMap<>();
            fillMap(ruleMap, rules);
          }
          return ruleMap;
        case CAUSAL_SCENARIO:
          if(scenarioMap == null){
            scenarioMap = new HashMap<>();
            fillMap(scenarioMap, scenarios);
          }
          return scenarioMap;
        default:
          return null;
        }
      
    }
    
    private void fillMap(Map<UUID,AbstractLtlProvider> entryMap,List<AbstractLtlProvider> list){
      if(list != null){
        for (AbstractLtlProvider provider : list) {
          entryMap.put(provider.getId(), provider);
        }
      }
      list = null;
    }
    
    /* (non-Javadoc)
     * @see xstampp.astpa.model.extendedData.IExtendedDataController#addRuleEntry(xstampp.astpa.model.interfaces.IExtendedDataModel.RuleType, xstampp.model.AbstractLtlProviderData, java.util.UUID, java.lang.String)
     */
    @Override
    public UUID addRuleEntry(IExtendedDataModel.ScenarioType ruleType,AbstractLtlProviderData data,UUID caID, String type){
      return addRuleEntry(getMap(ruleType), data, null, type);
    }

    /**
     * 
     * @param rule
     * 
     * @see IValueCombie
     * @return
     */
    public boolean addRefinedRule(AbstractLtlProvider rule){
        if(!getMap(ScenarioType.BASIC_SCENARIO).containsKey(rule.getId())){
          ruleIndex = Math.max(ruleIndex, rule.getNumber());
          return getMap(ScenarioType.BASIC_SCENARIO).put(rule.getId(),rule) != null;
        }
      return false;
    }
    
    /* (non-Javadoc)
     * @see xstampp.astpa.model.extendedData.IExtendedDataController#updateRefinedRule(java.util.UUID, xstampp.model.AbstractLtlProviderData, java.util.UUID)
     */
    @Override
    public boolean updateRefinedRule(UUID ruleId, AbstractLtlProviderData data,UUID linkedControlActionID){
      for(AbstractLtlProvider provider: getAllRefinedRules(true,true,true)){
        if(provider.getRuleId().equals(ruleId)){
          return updateRefinedRule(provider, data, linkedControlActionID);
        }
      }
      return false;
    }
    
    private boolean updateRefinedRule(AbstractLtlProvider provider, AbstractLtlProviderData data,UUID linkedControlActionID){
      boolean changed=false;
      changed = changed ||((RefinedSafetyRule) provider).setLtlProperty(data.getLtlProperty());
      changed = changed ||((RefinedSafetyRule) provider).setRefinedSafetyConstraint(data.getRefinedSafetyConstraint());
      changed = changed ||((RefinedSafetyRule) provider).setRefinedUCA(data.getRefinedUca());
      changed = changed ||((RefinedSafetyRule) provider).setSafetyRule(data.getSafetyRule());
      changed = changed ||((RefinedSafetyRule) provider).setUCALinks(data.getUcaLinks());
      changed = changed ||((RefinedSafetyRule) provider).setCaID(linkedControlActionID);
      changed = changed ||((RefinedSafetyRule) provider).setCriticalCombies(data.getCombies());
      return changed;
     
    }
    
    /* (non-Javadoc)
     * @see xstampp.astpa.model.extendedData.IExtendedDataController#getAllRefinedRules(boolean, boolean, boolean)
     */
    @Override
    public List<AbstractLtlProvider> getAllRefinedRules(boolean includeRules,
                                                        boolean includeScenarios,
                                                        boolean includeLTL){

      List<AbstractLtlProvider> tmp = new ArrayList<>();
      if(includeRules){
        tmp.addAll(getMap(ScenarioType.BASIC_SCENARIO).values());
      }
      if(includeScenarios){
        tmp.addAll(getMap(ScenarioType.CAUSAL_SCENARIO).values());
      }
      if(includeLTL){
        tmp.addAll(getMap(ScenarioType.CUSTOM_LTL).values());
      }
      return tmp;
    }

    /* (non-Javadoc)
     * @see xstampp.astpa.model.extendedData.IExtendedDataController#getAllRefinedRules(boolean, boolean, boolean)
     */
    @Override
    public AbstractLtlProvider getRefinedScenario(UUID ruleId) {
      if(getMap(ScenarioType.BASIC_SCENARIO).containsKey(ruleId)){
        return getMap(ScenarioType.BASIC_SCENARIO).get(ruleId);
      }if(getMap(ScenarioType.CAUSAL_SCENARIO).containsKey(ruleId)){
        return getMap(ScenarioType.CAUSAL_SCENARIO).get(ruleId);
      }if(getMap(ScenarioType.CUSTOM_LTL).containsKey(ruleId)){
        return getMap(ScenarioType.CUSTOM_LTL).get(ruleId);
      }
      return null;
    }
    /**
     * this calculates the type of rule of the ltl provider stored for that 
     * id
     * @param ruleId a valid rule id
     * @return the {@link ScenarioType} of the rule or null if the id is invalid
     */
    public ScenarioType getScenarioType(UUID ruleId){
      if(getMap(ScenarioType.BASIC_SCENARIO).containsKey(ruleId)){
        return ScenarioType.BASIC_SCENARIO;
      }if(getMap(ScenarioType.CAUSAL_SCENARIO).containsKey(ruleId)){
        return ScenarioType.CAUSAL_SCENARIO;
      }if(getMap(ScenarioType.CUSTOM_LTL).containsKey(ruleId)){
        return ScenarioType.CUSTOM_LTL;
      }
      return null;
    }
    /* (non-Javadoc)
     * @see xstampp.astpa.model.extendedData.IExtendedDataController#getAllRefinedRules(xstampp.model.IEntryFilter)
     */
    @Override
    public List<AbstractLtlProvider> getAllRefinedRules(IEntryFilter<AbstractLtlProvider> filter){
      List<AbstractLtlProvider> result = new ArrayList<>();
      for(AbstractLtlProvider data : getAllRefinedRules(true, true, true)){
        if(filter.check(data)){
          result.add(data);
        }
      }
      Collections.sort(result);
      return result;
    }
    
    private boolean removeEntry(Map<UUID,AbstractLtlProvider> entryMap, boolean removeAll, UUID id){
      boolean result = false;
        if(removeAll){
          //if removeAll than the rule index is set to 0 so the next rule is added with the index 0
          entryMap.clear();
          result = true;
        }else if(entryMap.containsKey(id)){
          // the rule which should be removed is searched for in both the 
          // general rules list and in the control actions
          result = entryMap.remove(id) != null;
        }
      return result;
    }
    
    @Override
    public boolean removeRefinedSafetyRule(ScenarioType type, boolean removeAll, UUID ruleId){
      return removeEntry(getMap(type), removeAll, ruleId);
    }

    public void prepareForExport() {
      prepareForSave();
    }

    public void prepareForSave() {
      if(ruleMap != null){
        rules = new ArrayList<>(ruleMap.values());
        ruleMap = null;
      }
      if(scenarioMap != null){
        scenarios = new ArrayList<>(scenarioMap.values());
        scenarioMap = null;
      }
      if(ltlMap != null){
        customLTLs = new ArrayList<>(ltlMap.values());
        ltlMap = null;
      }
    }
    
}
