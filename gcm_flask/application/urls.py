"""
urls.py

URL dispatch route mappings and error handlers

"""

from flask import render_template

from application import app
from application import views


## URL dispatch rules
# App Engine warm up handler
# See http://code.google.com/appengine/docs/python/config/appconfig.html#Warming_Requests
app.add_url_rule('/_ah/warmup', 'warmup', view_func=views.warmup)

# Register GCM Device ID
app.add_url_rule("/register", 'register', view_func=views.register, methods=['POST']) 

# Unregister GCM Device ID
app.add_url_rule("/unregister", 'unregister', view_func=views.unregister, methods=['POST'])
 
# admin-only Prep Message
app.add_url_rule('/prepMessage', 'prepMessage', view_func=views.prepMessage,methods=['POST','GET'])

# admin-only Send Message
app.add_url_rule('/sendMessage', 'sendMessage', view_func=views.sendMessage, methods=['POST'])


## Error handlers
# Handle 404 errors
@app.errorhandler(404)
def page_not_found(e):
    return render_template('404.html'), 404

# Handle 500 errors
@app.errorhandler(500)
def server_error(e):
    return render_template('500.html'), 500

