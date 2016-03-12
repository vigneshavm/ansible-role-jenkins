#!/usr/bin/env groovy

import groovy.json.*
import hudson.model.*
import jenkins.model.*


/**
    Convert Json string to Groovy Object

    @param String arg Json string to parse
    @return Map Groovy object used to get data
*/
def Map parse_data(String arg) {

    try {
        def JsonSlurper jsonSlurper = new JsonSlurper()
        return jsonSlurper.parseText(arg)
    }
    catch(Exception e) {
        throw new Exception("Parse data error, incoming data : ${arg}, "
                            + "error message : ${e.getMessage()}")
    }
}


/**
    Manage hipchat server configuration

    @param Descriptor Hipchat plugin descriptor
    @param Map Needed configuration
    @return Boolean True if configuration change everything, else false
*/
def Boolean manage_server(Descriptor desc, Map data) {

    try {

        // Get current server
        def String cur_server = desc.getServer()

        // Change setting if different value
        if (cur_server != data['server']) {
            desc.setServer(data['server'])
            return true
        }

        return false
    }
    catch(Exception e) {
        throw new Exception(
            'Manage hipchat server error, error message : ' + e.getMessage())
    }
}


/**
    Manage hipchat token configuration

    @param Descriptor Hipchat plugin descriptor
    @param Map Needed configuration
    @return Boolean True if configuration change everything, else false
*/
def Boolean manage_token(Descriptor desc, Map data) {

    try {

        // Get current token
        def String cur_token = desc.getToken()

        // Change setting if different value
        if (cur_token != data['token']) {
            desc.setToken(data['token'])
            return true
        }

        return false
    }
    catch(Exception e) {
        throw new Exception(
            'Manage hipchat token error, error message : ' + e.getMessage())
    }
}


/**
    Manage hipchat v2 enable configuration

    @param Descriptor Hipchat plugin descriptor
    @param Map Needed configuration
    @return Boolean True if configuration change everything, else false
*/
def Boolean manage_v2_enable(Descriptor desc, Map data) {

    try {

        // Get current setting
        def Boolean cur_setting = desc.isV2Enabled()

        // Change setting if different value
        if (cur_setting != data['v2_enabled']) {
            desc.setV2Enabled(data['v2_enabled'])
            return true
        }

        return false
    }
    catch(Exception e) {
        throw new Exception(
            'Manage hipchat v2 enable error, error message : '
            + e.getMessage())
    }
}


/**
    Manage hipchat room configuration

    @param Descriptor Hipchat plugin descriptor
    @param Map Needed configuration
    @return Boolean True if configuration change everything, else false
*/
def Boolean manage_room(Descriptor desc, Map data) {

    try {

        // Get current setting
        def String cur_room = desc.getRoom()

        // Change setting if different value
        if (cur_room != data['room']) {
            desc.setRoom(data['room'])
            return true
        }

        return false
    }
    catch(Exception e) {
        throw new Exception(
            'Manage hipchat room error, error message : ' + e.getMessage())
    }
}


/**
    Manage hipchat send as configuration

    @param Descriptor Hipchat plugin descriptor
    @param Map Needed configuration
    @return Boolean True if configuration change everything, else false
*/
def Boolean manage_send_as(Descriptor desc, Map data) {

    try {

        // Get current setting
        def String cur_send_as = desc.getSendAs()

        // Change setting if different value
        if (cur_send_as != data['send_as']) {
            desc.setSendHas(data['send_as'])
            return true
        }

        return false
    }
    catch(Exception e) {
        throw new Exception(
            'Manage hipchat send as error, error message : ' + e.getMessage())
    }
}


/* SCRIPT */

def List<Boolean> has_changed = []

try {
    def Jenkins jenkins_instance = Jenkins.getInstance()
    def desc = jenkins_instance.getDescriptor(
        'jenkins.plugins.hipchat.HipChatNotifier')

    // Get arguments data
    def Map data = parse_data(args[0])

    // Manage new configuration
    has_changed.push(manage_token(desc, data))
    has_changed.push(manage_v2_enable(desc, data))
    has_changed.push(manage_room(desc, data))
    has_changed.push(manage_send_as(desc, data))

    // Save new configuration to disk
    desc.save()
    jenkins_instance.save()
}
catch(Exception e) {
    throw new RuntimeException(e.getMessage())
}

// Build json result
result = new JsonBuilder()
result {
    changed has_changed.any()
    output {
        changed has_changed
    }
}

println result

