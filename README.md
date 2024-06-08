# Devjobs web app

## Table of contents

- [Overview](#overview)
  - [The challenge](#the-challenge)
  - [Screenshot](#screenshot)
  - [Links](#links)
- [My process](#my-process)
  - [Built with](#built-with)
  - [What I learned](#what-i-learned)
  - [Continued development](#continued-development)
  - [Useful resources](#useful-resources)
- [Author](#author)
- [Acknowledgments](#acknowledgments)

## Overview

### The challenge

The challenge is to build out this jobs board using local files `data.json` and `company.json` to retrieve the data. 

Your users should be able to:

- Be able to filter jobs on the index page by title, location, whether a job is for a full-time position and also sort the jobs by most recent.
- Be able to click a job from the index page so that they can read more information and apply for the job
- Be able to click into the company 'website' (Just a routerlink) so that they can read more information about the company, including the map location and reviews.

### The technical challenge

This project is built using 
- Angular 17
- Java & Spring Boot
- Docker
- MongoDB, S3, MySQL

- User should be able to filter out the jobs
- Click into the individual jobs (which is just routerlink)
- From the individual job, click into the company's website (which is also just a routerlink)
- From the individual job, they can click 'Apply Now' which will lead them to a form where they can fill in their details and upload their details
- Upon successful application, user will be shown that they have successfully applied.
- The resumes and other documents will be uploaded to S3, which will in turn return a url.
- Then this url, with other details, I can save to MySQL.
- Back in the homepage, there should be a link 'applied jobs' to see the list of jobs the user has applied.

### Screenshot

![](./screenshot.jpg)

### Links

- Solution URL: [Add solution URL here](https://your-solution-url.com)
- Live Site URL: [Add live site URL here](https://your-live-site-url.com)

## My process

### Built with

- [Angular](https://reactjs.org/) - JS library
- [Spring Boot](https://nextjs.org/) - React framework
- [Bootstrap](https://styled-components.com/) - For styles
- [S3 Bucket]
- [MongoDB]
- [MySQL]