How generated test resources.zip has been generated
---------------------------------------------------

The whole steps don't have to be repeated when 'upgrading' the home zip,
they are just documented for replicability.

1. Start Confluence 7.5.0 with `./mvnw clean package confluence:run` and login with 'admin:admin'
2. Change baseurl to localhost if necessary
3. Close license health check
4. Create user:
   ```
   username: user
   fullname: user
   email: user@example.com
   password: user
   ```
   with only default group 'confluence-users'
5. Shut down using `[Ctrl]+[D]`
6. Save home ZIP with `./mvnw confluence:create-home-zip` and copy
   `./target/confluence/generated-test-resources.zip` to `src/test/resources/`

7. ...
