/**
 *  Child pH Sensor
 *
 *  Copyright 2017 Marcus van Ierssel
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
 *    2017-04-19  Dan Ogorchock  Original Creation
 *    2017-08-23  Allan (vseven) Added a generateEvent routine that gets info from the parent device.  This routine runs each time the value is updated which can lead to other modifications of the device.
 *    2018-06-02  Dan Ogorchock  Revised/Simplified for Hubitat Composite Driver Model
 *    2020-11-10  Marcus van Ierssel	copied/modified for pH
 * 
 */
metadata {
	definition (name: "Child pH Sensor", namespace: "ogiewon", author: "Marcus van Ierssel") {
		capability "pH Measurement"
		capability "Sensor"

		attribute "lastUpdated", "String"
	}

	simulator {

	}

	tiles(scale: 2) {
		multiAttributeTile(name: "pH", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.pH", key: "PRIMARY_CONTROL") {
				attributeState("pH", label: 'pH ${currentValue}', defaultState: true, backgroundColors: [
                                // Celsius
                                [value: 6, color: "#dc243e"],
                                [value: 7, color: "#79b821"],
                                [value: 8, color: "#dc243e"]
						])
			}
 			tileAttribute("device.lastUpdated", key: "SECONDARY_CONTROL") {
    				attributeState("default", label:'    Last updated ${currentValue}',icon: "st.Health & Wellness.health9")
            }
		}
        valueTile("phMain", "device.pH", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state("default", label:'pH ${currentValue}', icon:"st.alarm.water.wet")
        }

        main "phMain"
        details(["pH"])

	}
}

def parse(String description) {
    log.debug "parse(${description}) called"
	def parts = description.split(" ")
    def name  = parts.length>0?parts[0].trim():null
    def value = parts.length>1?parts[1].trim():null
    if (name && value) {
        // Update device
        sendEvent(name: "pH", value: value)
        //sendEvent(name: name, value: value)
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