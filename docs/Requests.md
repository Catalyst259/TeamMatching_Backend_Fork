# Login
curl --location --request POST 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{ "account": "tm_user101@example.com", "password": "123456" }'