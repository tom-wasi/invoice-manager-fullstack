
# Invoice Manager

This is a web app that allows you to upload your company(ies) invoices and send them to your acountant with just one click.
After creating an account and confirming it via e-mail message, you can create a company attach to it the company's accountant e-mail address. When it's time to settle the invoices, just mark the ones that you want to send and click **send**. The invoices will then appear as **settled**.



## Tech Stack

**Client:** ![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)  ![NPM](https://img.shields.io/badge/NPM-%23CB3837.svg?style=for-the-badge&logo=npm&logoColor=white)
![Chakra](https://img.shields.io/badge/chakra-%234ED1C5.svg?style=for-the-badge&logo=chakraui&logoColor=white)
![Vite](https://img.shields.io/badge/vite-%23646CFF.svg?style=for-the-badge&logo=vite&logoColor=white)

**Server:** ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) 
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)

**Database:** ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)

**Bucket:** AWS S3

**Auth:** ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

**Testing:** JUnit, Testcontainers




	
## Environment Variables

To run this project, you will need to add the following environment variables to your .env.local (can also be .env) file in the root directory

`MAIL_USERNAME` : this is the e-mail address the server will use to send the confirmation message when creating a new account

`MAIL_PASSWORD` : this is the password you'll need to generate on the e-mail account. For more info on generating password for gmail account visit https://knowledge.workspace.google.com/kb/how-to-create-app-passwords-000009237

`MAIL_PORT` : you should pick 587 here, i kept it in .env without a particular reason

`DATASOURCE_URL` : database URL

`DATASOURCE_USERNAME` : datbase username

`DATASOURCE_PASSWORD` : database password

`SECRET_KEY` : JWT secret key. You'll need to generate it. For more info visit https://jwt.io/introduction

`AWS_REGION` : the AWS account's region. Need to be specified to be able to use the S3 bucket.

`AWS_S3_BUCKETS` : the name of the bucket. For more info visit https://aws.amazon.com/s3/

## And for the React root folder .env.local file

`VITE_API_BASE_URL` : this is the server's URL

## Installation

For the server part you'll need to create a database. Here's how you can do it with docker:

```bash
docker pull postgres

docker run --name invoice-manager-db -e POSTGRES_PASSWORD=password -d postgres

```
Then, you can for example create a new database user and grant him permissions and roles, or if you don't care - just connect as postgres user.

To get inside the container you can:

```bash
docker exec -it postgres bash
```




Install invoice-manager-app with npm

```bash
git clone https://github.com/tom-wasi/invoice-manager-fullstack.git

cd frontend/vite/invoice-manager-app

npm install
```
    
To run the project

```bash
npm run dev
```
## Appendix

Please note that there are some bugs that require fixing - and they will be fixed. This app was made for two reasons: to add it to my portfolio and to deploy it and use it - which will be made once the rest of the bugs are fixed :)

