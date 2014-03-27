"""RequestHandlers for starter project."""

__author__ = 'alainv@google.com (Alain Vongsouvanh)'


# Add the library location to the path
import sys
sys.path.insert(0, 'lib')

import webapp2

from attachmentproxy.handler import ATTACHMENT_PROXY_ROUTES
from main_handler import MAIN_ROUTES
from notify.handler import NOTIFY_ROUTES
from oauth.handler import OAUTH_ROUTES
from signout.handler import SIGNOUT_ROUTES


ROUTES = (
    ATTACHMENT_PROXY_ROUTES + MAIN_ROUTES + NOTIFY_ROUTES + OAUTH_ROUTES +
    SIGNOUT_ROUTES)


app = webapp2.WSGIApplication(ROUTES)
