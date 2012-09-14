"""
views.py

URL route handlers

Note that any handler params must match the URL route params.
For example the *say_hello* handler, handling the URL route '/hello/<username>',
  must be passed *username* as the argument.

"""


from google.appengine.api import users
from google.appengine.api import taskqueue
from google.appengine.runtime.apiproxy_errors import CapabilityDisabledError

from flask import render_template, flash, url_for, redirect

from models import ExampleModel
from decorators import login_required, admin_required
from forms import ExampleForm

from flask import request
from application.models import RegIDModel
from google.appengine.ext import db
import string
from application.forms import RegIDForm

import json
from werkzeug.datastructures import MultiDict
import urllib2
from flask.wrappers import Response
from werkzeug_debugger_appengine import get_template
from flask.helpers import send_file
import logging

YOUR_API_KEY = "Your Api key"

def warmup():
    """App Engine warmup handler
    See http://code.google.com/appengine/docs/python/config/appconfig.html#Warming_Requests

    """
    return ''


def register():
    """Register device for GCM

    """
    if request.method == 'POST':
        regIDForm = RegIDForm()
        print " data " + regIDForm.regID.data
        if regIDForm.regID != None: #regIDForm.validate_csrf_token("redID"):
            q = RegIDModel.all()
            q.filter("regID =", regIDForm.regID.data)
            result = q.get()
            if result == None:
                regID = RegIDModel(regID=regIDForm.regID.data)
                print regID.put()
                return "success"
    return 'failure'


def unregister():
    """Unregister device for GCM

    """
    if request.method == 'POST':
        regID = request.form['regID']
        regID = string.replace(regID, '\'', '\\\'')
        regIDModel = db.GqlQuery("SELECT * FROM RegIDModel WHERE regID ='" + regID)
        regIDModel.run()
        regIDEntries = regIDModel.index_list()
        if(len(regIDEntries) > 0):
            db.delete(regIDEntries.pop())
        return "success"
    return 'failure'

def sendMessage(dictObj):
    """Send Message for GCM

    """
    if True:
        data = json.dumps(dictObj, sort_keys=True, indent=4)
        clen = len(data)
        headers = MultiDict()
        headers['Content-Type'] = 'application/json'
        headers['Content-Length'] = clen
        headers['Authorization'] = "key=" + YOUR_API_KEY
        
        req = urllib2.Request("https://android.googleapis.com/gcm/send", data, headers)
        f = urllib2.urlopen(req)
        responseMsg = f.read()
        f.close()
        return responseMsg;
        return json.dumps(dictObj, sort_keys=True, indent=4)

@admin_required
def prepMessage():
    """Prepare Message for GCM

    """
    if request.method == "GET":
        return render_template("prepmessage.html")
    elif request.method == "POST":
        outDict = MultiDict();
        params = dict();
        params['messageType'] = request.form['messageType']
        params['message'] = request.form['message']
        outString = ""
        
        q = RegIDModel.all()
        count = q.count(1000000)
        iCount = 0
        while iCount < count:
            items = q.fetch(100, iCount);            
            strings = ""
            start = ""
            stringarray = []
            for item in items:
                strings = strings + start + item.regID
                start = "," 
                stringarray.append(item.regID)
            outDict['registration_ids'] = stringarray
            outDict['data'] = params
            outString += sendMessage(outDict) +"\n"
            iCount += 100;
        return outString
    return ''
