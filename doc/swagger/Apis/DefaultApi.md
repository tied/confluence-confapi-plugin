# DefaultApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**addDirectory**](DefaultApi.md#addDirectory) | **PUT** /directories | Adds a new user directory
[**getDirectories**](DefaultApi.md#getDirectories) | **GET** /directories | Retrieves user directory information
[**getPing**](DefaultApi.md#getPing) | **GET** /ping | 
[**getPopMailServer**](DefaultApi.md#getPopMailServer) | **GET** /mail/pop | 
[**getSettings**](DefaultApi.md#getSettings) | **GET** /settings | 
[**getSmtpMailServer**](DefaultApi.md#getSmtpMailServer) | **GET** /mail/smtp | 
[**putPopMailServer**](DefaultApi.md#putPopMailServer) | **PUT** /mail/pop | 
[**putSettings**](DefaultApi.md#putSettings) | **PUT** /settings | 
[**putSmtpMailServer**](DefaultApi.md#putSmtpMailServer) | **PUT** /mail/smtp | 


<a name="addDirectory"></a>
# **addDirectory**
> UserDirectoryBean addDirectory(userDirectoryBean, test)

Adds a new user directory

    Upon successful request, returns the added UserDirectoryBean object, Any existing configurations with the same name property are removed before adding the new configuration

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **userDirectoryBean** | [**UserDirectoryBean**](\Models/UserDirectoryBean.md)| The user directory to add |
 **test** | **Boolean**| Whether or not to test the connection to the user directory service, e.g. CROWD (null defaults to TRUE) | [optional] [default to null]

### Return type

[**UserDirectoryBean**](\Models/UserDirectoryBean.md)

### Authorization

[http_basic_scheme](../README.md#http_basic_scheme)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="getDirectories"></a>
# **getDirectories**
> UserDirectoryBean getDirectories()

Retrieves user directory information

    Upon successful request, returns a list of UserDirectoryBean object containing user directory details

### Parameters
This endpoint does not need any parameter.

### Return type

[**UserDirectoryBean**](\Models/UserDirectoryBean.md)

### Authorization

[http_basic_scheme](../README.md#http_basic_scheme)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getPing"></a>
# **getPing**
> getPing()



### Parameters
This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization

[http_basic_scheme](../README.md#http_basic_scheme)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getPopMailServer"></a>
# **getPopMailServer**
> getPopMailServer()



### Parameters
This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization

[http_basic_scheme](../README.md#http_basic_scheme)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getSettings"></a>
# **getSettings**
> getSettings()



### Parameters
This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization

[http_basic_scheme](../README.md#http_basic_scheme)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getSmtpMailServer"></a>
# **getSmtpMailServer**
> getSmtpMailServer()



### Parameters
This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization

[http_basic_scheme](../README.md#http_basic_scheme)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="putPopMailServer"></a>
# **putPopMailServer**
> putPopMailServer(popMailServerBean)



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **popMailServerBean** | [**PopMailServerBean**](\Models/PopMailServerBean.md)|  | [optional]

### Return type

null (empty response body)

### Authorization

[http_basic_scheme](../README.md#http_basic_scheme)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="putSettings"></a>
# **putSettings**
> putSettings(settingsBean)



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **settingsBean** | [**SettingsBean**](\Models/SettingsBean.md)|  | [optional]

### Return type

null (empty response body)

### Authorization

[http_basic_scheme](../README.md#http_basic_scheme)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="putSmtpMailServer"></a>
# **putSmtpMailServer**
> putSmtpMailServer(smtpMailServerBean)



### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **smtpMailServerBean** | [**SmtpMailServerBean**](\Models/SmtpMailServerBean.md)|  | [optional]

### Return type

null (empty response body)

### Authorization

[http_basic_scheme](../README.md#http_basic_scheme)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

