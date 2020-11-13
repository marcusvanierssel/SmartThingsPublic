/**
 *  Child Fan
 *
 *  Copyright 2020 Marcus van Ierssel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2018-06-02  MVI	  		New
 */
metadata {
	definition (name: "Child Fan", namespace: "ogiewon", author: "Marcus van Ierssel", cstHandler: true, ocfDeviceType: "oic.d.fan", mnmn: "SmartThingsCommunity", vid: "generic-fan") {
		capability "Fan Speed" 
   		//capability "Switch"
		capability "Actuator"
		capability "Sensor"

		command "fanoff"
		command "low"
        command "med"
        command "high"
        command "lightOn"
        command "lightOff"
        command "on"
        command "off"
        command "setFanSpeed"
		attribute "lastUpdated", "String"
  		attribute "lightState", "String"
        attribute "powerState", "String"
	}

	simulator {

	}

    tiles(scale: 2) {
        multiAttributeTile(name:"fan", type: "generic", width: 3, height: 4) {
            tileAttribute("device.fanSpeed", key: "PRIMARY_CONTROL") {
                attributeState "0",  label: '',  action: "low", icon: "st.thermostat.fan-off", backgroundColor: "#ffffff", nextState: "low", defaultState: true
                attributeState "1",  label: 'Low',  action: "med",  icon: "st.Lighting.light24", backgroundColor: "#f1d801", nextState: "med"
                attributeState "2",  label: 'Med',  action: "high", icon: "st.Lighting.light24", backgroundColor: "#d04e00", nextState: "high"
                attributeState "3", label: 'High', action: "fanoff", icon: "st.Lighting.light24", backgroundColor: "#bc2323", nextState: "off"
            }
            tileAttribute("device.lastUpdated", key: "SECONDARY_CONTROL") {
                attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
        }
        standardTile("fanlow", "device.fanSpeed", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("statelow", label:'Low', action:"low")
        }
        standardTile("fanmed", "device.fanSpeed", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("statemed", label:'Med', action:"med")
        }
        standardTile("fanhigh", "device.fanSpeed", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("statehigh", label:"High", action:"high")
        }
        standardTile("light", "device.lightState", width: 3, height: 2, decoration: "flat") {
    		state "off", label: "off", icon: "st.Lighting.light13", backgroundColor: "#ffffff", action: "lightOff", nextState: "on", defaultState: true
    		state "on",  label: "on",  icon: "st.Lighting.light11",  backgroundColor: "#00a0dc", action: "lightOn", nextState: "off"
		}
        standardTile("power", "device.powerState", width: 3, height: 2, decoration: "flat") {
    		state "off", label: "", icon: "st.thermostat.heating-cooling-off", backgroundColor: "#ffffff", action: "off", nextState: "on", defaultState: true
    		state "on",  label: "",  icon: "st.thermostat.fan-on",  backgroundColor: "#00a0dc", action: "on", nextState: "off"
		}
        standardTile("refresh", "device.fanSpeed", width: 3, height: 2, decoration: "flat") {
    		state "refresh", label: "refresh", icon: "st.secondary.refresh", backgroundColor: "#ffffff", action: "refresh"
		}
        main "fan"
        details(["fan", "fanlow", "fanmed", "fanhigh", "light", "power"])
    }
}

def fanoff() {    setFanSpeed(0)}
def low() {    setFanSpeed(1)}
def med() {    setFanSpeed(2)}
def high() {    setFanSpeed(3)}

def lightOn() {
    sendData("lightOn")
    sendEvent(name: "lightState", value: "on", displayed: false)
}

def lightOff() {
	//device.fanSpeed = 4
	def spd = device.currentValue("fanSpeed")
    def lgt = device.currentValue("lightState")
	log.debug "lightOff"
    log.debug "lightState: $lgt  speed: $spd"
    sendData("lightOff")
    sendEvent(name: "lightState", value: "off", displayed: false)
}

def setFanSpeed(int speed) {
	log.debug "setFanSpeed >> speed: $speed"
	def level = Math.max(Math.min(speed, 3), 0) //+ 4*(lightState as Integer)
    sendData("${level}")
    //sendEvent(name: "fanSpeed", value: level, displayed: false)
}

def sendData(String value) {
    def name = device.deviceNetworkId.split("-")[-1]
    parent.sendData("${name} ${value}")  
}

def parse(String description) {
    log.debug "parse(${description}) called"
	def parts = description.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null
    if (name && value) {
  	    if (name == "fan") {
   			name = "fanSpeed"  
            value = value as Integer
		}
        // Update device
        log.debug "name: ${name} value: ${value}"
        sendEvent(name: name, value: value)
        // Update lastUpdated date and time
        def nowDay = new Date().format("MMM dd", location.timeZone)
        def nowTime = new Date().format("h:mm a", location.timeZone)
        sendEvent(name: "lastUpdated", value: nowDay + " at " + nowTime, displayed: false)
    }
    else {
    	log.debug "Missing either name or value.  Cannot parse!"
    }
}

def installed() {
}