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
//	definition (name: "ch bl3", namespace: "ogiewon", author: "Marcus van Ierssel", cstHandler: true) {
	definition (name: "Child Blind", namespace: "ogiewon", author: "Marcus van Ierssel") {
		capability "Window Shade"
		capability "Actuator"
		capability "Sensor"

		command "stop"
        command "setShadeLevel"
        
		attribute "lastUpdated", "String"
        attribute "ShadeLevel", "number"
	}

	simulator {

	}

	tiles(scale: 2) {
		multiAttributeTile(name:"blind", type: "generic", width: 6, height: 4){
			tileAttribute ("device.windowShade", key: "PRIMARY_CONTROL") {
				attributeState "open", label: '${name}', action: "windowShade.close", icon: "https://raw.githubusercontent.com/a4refillpad/media/master/blind-open.png", backgroundColor: "#ffffff", nextState:"closed"
				attributeState "closed", label: '${name}', action: "windowShade.open", icon: "https://raw.githubusercontent.com/a4refillpad/media/master/blind-closed.png", backgroundColor: "#00A0DC", nextState:"open"
//				attributeState "closing", label:'${name}', action:"windowShade.open", icon:"st.blinds.blind.close", backgroundColor:"#00A0DC", nextState:"opening"
//				attributeState "opening", label:'${name}', action:"windowShade.close", icon:"st.blinds.blind.open", backgroundColor:"#ffffff", nextState:"closing"
                attributeState "partially open", label:'${name}', action:"windowShade.open", icon:"https://raw.githubusercontent.com/a4refillpad/media/master/blind-part-open.png", backgroundColor:"#ffffff", nextState:"opening"
			}
   			//tileAttribute ("device.level", key: "SLIDER_CONTROL") {
			//	attributeState "level", action:"switch level.setLevel"
			//}
			tileAttribute("device.lastUpdated", key: "SECONDARY_CONTROL") {
                attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
            }

		}
        standardTile("stateopen", "device.windowShade", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("stateopen", label:'open', action:"windowShade.open", icon:"https://raw.githubusercontent.com/a4refillpad/media/master/blind-open.png")
        }
        standardTile("stateclose", "device.windowShade", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("stateclose", label:'close', action:"windowShade.close", icon:"https://raw.githubusercontent.com/a4refillpad/media/master/blind-closed.png")
        }
        standardTile("statestop", "device.windowShade", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("statestop", label:"", action:"stop", icon:"st.sonos.stop-btn")
        }
        controlTile("levelSliderControl", "device.ShadeLevel", "slider", height: 1, width: 6, inactiveLabel: false) {
            state("level", label: '${currentValue}', action:"setShadeLevel")
        }

 		//valueTile("level", "device.level", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		//	state "level", label:'${currentValue} %', unit:"%", backgroundColor:"#ffffff"
		//}
 		//valueTile("lastUpdated", "device.lastUpdated", inactiveLabel: false, decoration: "flat", width: 4, height: 2) {
    	//	state "default", label:'Last Updated ${currentValue}', backgroundColor:"#ffffff"
        //}
       
		main(["blind"])
		details(["blind", "levelSliderControl", "stateopen", "statestop", "stateclose"])       
	}
}
def close() {
    sendData("close")
    sendEvent(name: "ShadeLevel", value: "100", displayed: false)
}

def open() {
    sendData("open")
    sendEvent(name: "ShadeLevel", value: "0", displayed: false)
}

def stop() {
    sendData("stop")
    sendEvent(name: "ShadeLevel", value: "50", displayed: false)
    //sendEvent(name: "windowShade", value: "partially open", displayed: false)

}

def setShadeLevel(int value) {
	log.debug "setShadeLevel >> value: $value"
	//def valueaux = value as Integer
	def level = Math.max(Math.min(value, 100), 0)
    sendData("${level}")
    sendEvent(name: "ShadeLevel", value: level, displayed: false)
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