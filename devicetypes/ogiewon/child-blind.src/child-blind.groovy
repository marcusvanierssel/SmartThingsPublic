/**
 *  Child Blind
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
	definition (name: "Child Blind", namespace: "ogiewon", author: "Dan Ogorchock", vid: "generic-shade") {
    //definition (name: "Child Blind", namespace: "ogiewon", author: "Marcus van Ierssel", ocfDeviceType: "oic.d.blind", mnmn: "SmartThings", vid: "generic-shade") {
		capability "Window Shade"
        capability "Window Shade Level"
		capability "Actuator"
		capability "Sensor"

		command "stop"
        command "setShadeLevel"        
        attribute "shadeLevel", "number"
	}
}
def close() {
    sendData("close")
    sendEvent(name: "shadeLevel", value: "100", displayed: false)
}

def open() {
    sendData("open")
    sendEvent(name: "shadeLevel", value: "0", displayed: false)
}

def stop() {
    sendData("stop")
    sendEvent(name: "shadeLevel", value: "50", displayed: false)
    //sendEvent(name: "windowShade", value: "partially open", displayed: false)
}

def setShadeLevel(int value) {
	log.debug "setShadeLevel >> value: $value"
	//def valueaux = value as Integer
	def level = Math.max(Math.min(value, 100), 0)
    sendData("${level}")
    sendEvent(name: "shadeLevel", value: level, displayed: false)
    //sendEvent(name: "windowShade", value: "partially open", displayed: false)
}

def sendData(String value) {
	log.debug "sendData value: $value"
    def name = device.deviceNetworkId.split("-")[-1]
    parent.sendData("${name} ${value}")  
}

def parse(String description) {
    log.debug "parse(${description}) called"
	def parts = description.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null
    if (name && value) {
	    if (name == "blind")
    		name = "windowShade"    
    	if (value == "partially")
    		value = "partially open"
    	log.debug "name: ${name} value: ${value}"
        // Update device
        sendEvent(name: name, value: value)
    }
    else {
    	log.debug "Missing either name or value.  Cannot parse!"
    }
}

def installed() {
}

def updated() {
}