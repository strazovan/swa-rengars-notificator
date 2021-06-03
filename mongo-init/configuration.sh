#!/bin/bash
set -e

mongo <<EOF
use $MONGO_INITDB_DATABASE
db.createCollection('configurations', { capped: false });
db.configurations.insert({
  'type':'TEST_MAIL_NOTIFICATION',
  'notificatorName':'MailNotificator',
  'notificatorParameters':{
    'from':'$FROM_MAIL_ADDRESS',
    'subject': 'Hello there'
  },
  'template':'Dear \${name}, hi. Thanks, bye :)'
});
db.configurations.insert({
  'type':'JOB_OFFER_CREATED',
  'notificatorName':'MailNotificator',
  'notificatorParameters':{
    'from':'$FROM_MAIL_ADDRESS',
    'subject': 'New marketplace offer!'
  },
  'template':'There is a new marketplace offer from company \${companyName}: \n\${description}.'
});
db.configurations.insert({
  'type':'NEW_USER_ACCOUNT_REGISTERED',
  'notificatorName':'MailNotificator',
  'notificatorParameters':{
    'from':'$FROM_MAIL_ADDRESS',
    'subject': 'Welcome'
  },
  'template':'Dear \${name}, thank you for your registration. Bye :)'
});


EOF