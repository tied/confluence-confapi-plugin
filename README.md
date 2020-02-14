[![ASERVO Software GmbH](https://aservo.github.io/img/aservo_atlassian_banner.png)](https://www.aservo.com/en/atlassian)

ConfAPI for Confluence
======================

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.aservo.atlassian/confluence-confapi-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.aservo.atlassian/confluence-confapi-plugin)
[![Build Status](https://circleci.com/gh/aservo/confluence-confapi-plugin.svg?style=shield)](https://circleci.com/gh/aservo/confluence-confapi-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=aservo_confluence-confapi-plugin&metric=coverage)](https://sonarcloud.io/dashboard?id=aservo_confluence-confapi-plugin)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=aservo_confluence-confapi-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=aservo_confluence-confapi-plugin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

REST API for automated Confluence configuration.

https://developer.atlassian.com/display/DOCS/Introduction+to+the+Atlassian+Plugin+SDK

Resources
---------

All resources produce JSON (media type:  `application/json`) results.

### Settings

Access general Confluence settings like the base url or the title.

* #### `GET /rest/confapi/1/settings`

  Get Confluence application settings.

  __Responses__

  ![Status 200][status-200]

  ```javascript
  {
    "baseurl": "http://localhost:1990/confluence",
    "title": "Your Confluence"
  }
  ```

  ![Status 401][status-401]

  Returned if the current user is not authenticated.

  ![Status 403][status-403]

  Returned if the current user is not an administrator.

* #### `PUT /rest/confapi/1/settings`

  Set Confluence application settings.

  __Request Body__

  Media type: `application/json`

  Content: Settings, for example:

  ```javascript
  {
    "baseurl": "http://localhost:1990/confluence",
    "title": "Your Confluence"
  }
  ```

  __Request Parameters__

  None.

  __Responses__

  ![Status 200][status-200]

  Returned if request could be executed without any exceptions.

  ![Status 401][status-401]

  Returned if the current user is not authenticated.

  ![Status 403][status-403]

  Returned if the current user is not an administrator.

### SMTP Mail Server

Although this does not always seem to make any sense, Confluence allows
defining multiple mail servers. This REST API only allows creating,
updating and deleting one single mail server.

* #### `GET /rest/confapi/1/mail/smtp`

  Get the configuration of the SMTP mail server, if any server is defined.

  __Responses__

  ![Status 200][status-200]

  ```javascript
  {
    "name": "Localhost",
    "description": "The localhost SMTP server",
    "from": "confluence@localhost",
    "prefix": "Confluence",
    "protocol": "smtp",
    "host": "localhost",
    "port": 25,
    "tls", false,
    "timeout": 10000,
    "username": "admin",
    "password": "admin"
  }
  ```

  ![Status 401][status-401]

  Returned if the current user is not authenticated.

  ![Status 403][status-403]

  Returned if the current user is not an administrator.

  ![Status 404][status-404]

  Returned if no SMTP mail server is configured.

* #### `PUT /rest/confapi/1/mail/smtp`

  Set the configuration of the SMTP mail server.

  __Request Body__

  Media type: `application/json`

  Content: Settings, for example:

  ```javascript
  {
    "name": "Localhost",
    "description": "The localhost SMTP server",
    "from": "confluence@localhost",
    "prefix": "Confluence",
    "protocol": "smtp",
    "host": "localhost",
    "port": 25,
    "tls", false,
    "timeout": 10000,
    "username": "admin",
    "password": "admin"
  }
  ```

  __Request Parameters__

  None.

  __Responses__

  ![Status 200][status-200]

  Returned if the request could be executed without any exceptions.

  ![Status 400][status-400]

  Returned if the request caused exceptions.

  The response will contain a list of errors that occurred while setting
  some specific values such as a string that was too long, for example:

  ```
  {
    "errorMessages": [
        "..."
    ]
  }
  ```

  ![Status 401][status-401]

  Returned if the current user is not authenticated.

  ![Status 403][status-403]

  Returned if the current user is not an administrator.

### POP Mail Server

Although this does not always seem to make any sense, Confluence allows
defining multiple mail servers. This REST API only allows creating,
updating and deleting one single mail server.

* #### `GET /rest/confapi/1/mail/pop`

  Get the configuration of the POP mail server, if any server is defined.

  __Responses__

  ![Status 200][status-200]

  ```javascript
  {
    "name": "Localhost",
    "description": "The localhost SMTP server",
    "protocol": "pop",
    "host": "localhost",
    "port": 110,
    "timeout": 10000,
    "username": "admin",
    "password": "admin"
  }
  ```

  ![Status 401][status-401]

  Returned if the current user is not authenticated.

  ![Status 403][status-403]

  Returned if the current user is not an administrator.

  ![Status 404][status-404]

  Returned if no POP mail server is configured.

* #### `PUT /rest/confapi/1/mail/pop`

  Set the configuration of the POP mail server.

  __Request Body__

  Media type: `application/json`

  Content: Settings, for example:

  ```javascript
  {
    "name": "Localhost",
    "description": "The localhost SMTP server",
    "protocol": "smtp",
    "host": "localhost",
    "port": 100,
    "timeout": 10000,
    "username": "admin",
    "password": "admin"
  }
  ```

  __Request Parameters__

  None.

  __Responses__

  ![Status 200][status-200]

  Returned if the request could be executed without any exceptions.

  ![Status 400][status-400]

  Returned if the request caused exceptions.

  The response will contain a list of errors that occurred while setting
  some specific values such as a string that was too long, for example:

  ```
  {
    "errorMessages": [
        "..."
    ]
  }
  ```

  ![Status 401][status-401]

  Returned if the current user is not authenticated.

  ![Status 403][status-403]

  Returned if the current user is not an administrator.

[status-200]: https://img.shields.io/badge/status-200-brightgreen.svg
[status-400]: https://img.shields.io/badge/status-400-red.svg
[status-401]: https://img.shields.io/badge/status-401-red.svg
[status-403]: https://img.shields.io/badge/status-403-red.svg
[status-404]: https://img.shields.io/badge/status-404-red.svg
