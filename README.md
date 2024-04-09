# iris-track-uploader
Program to create youtube video, upload to Youtube, upload files to google drive and S3, and create website product.

## April 2024 Update
- Fixed Drive Token Expiration Error, recursive call did not return correctly
- Added Call to S3 Upload Script and the returned Url is used in the Website upload

## Program Workflow
![process](https://github.com/h3music/iris-track-uploader/assets/22086435/408bb0be-49f0-48c7-9edf-f9cbc8616e7c)

### Read CSV File
The CSV File stores unique product/track instant parameters. These include Product Name, Tags, Tonality, Publish Date, Local Media Location, and Local Artwork Location.

### Get Metadata from WAV File
The producer exports the audio track as an Mp3, a WAV File, and WAV Trackouts (each instrument on its own file). The DAW program for producing music tracks exports WAV Files with ACID chunks, which store music production information. This program portion accesses this acid chunk in the product WAV File, for later use.
Further information is available on my [WavFile](https://github.com/aabalke33/wavFile) repo.

### Create Zip File
Create zip from WAV trackouts through a standard implementation.

### Upload to Drive & S3
Upload Mp3, Artwork, WAV, and Zip Files to Google Drive through the Google Drive API. Additionally uploads Mp3 to S3 using a custom Node package, connected through stdin and stdout.

### Create Website Product
Create Website Product on Wordpress / Woocommerce using REST APIs.

### Create Video
Create video to upload to youtube. Uses FFMPEG, and custom Frame manipulation packages. Requires a grain effect throughout video and glitch effects at Choruses.
See [Grain Effects](https://github.com/aabalke33/film-grain-effect) and [Glitch Effects](https://github.com/aabalke33/rgb-offset) for detail breakdowns of the effects.

### Upload Video
Upload video, and thumbnail Files to Youtube through the Youtube API. Also 

## Deliverables

Website Product | Youtube Video
:-------------------------:|:-------------------------:
[![website](https://github-production-user-asset-6210df.s3.amazonaws.com/22086435/260809691-2c425952-f246-4a1e-b7cf-00626e5c645f.png)](https://h3music.com/product/downsides/)  |  [<img src="https://github-production-user-asset-6210df.s3.amazonaws.com/22086435/260809675-27855db3-16ee-4d51-a7c2-834becbc9683.jpg" width="100%">](https://www.youtube.com/watch?v=wr3tokbvArI)

## Youtube Breakdown
[<img src="https://github-production-user-asset-6210df.s3.amazonaws.com/22086435/260810511-b89b7f50-182d-4827-8691-ae2245943d64.jpg" width="50%">](https://www.youtube.com/watch?v=DtZYZEKrGsw)

## Dependencies
1. FFMPEG (Built Using 2023-06-11-git-09621fd7d9-full_build- gyan.dev)
2. com.google.api-client:google-api-client:1.23.0
3. com.google.oauth-client:google-oauth-client-jetty:1.23.0
4. com.google.apis:google-api-services-youtube:v3-rev222-1.25.0
5. com.google.apis:google-api-services-drive:v3-rev20230206-2.0.0
6. https://github.com/calatonsystems/wc-api-java (included in project file)

## Roadmap
- Create User-Friendly Interface
- Multithread Zip file creation
- Provide further error checking and data validation
- Automate new Google Drive location every year, currently is a manual process

## Found Bugs
- Only adds one category from tags to website when multiple are present
- Only allows for 8 Labels to be read from WAV File ACID Chunk
- If BPM and Tempo fluctuates ACID Chunk provides inaccurate value

## Roadmap (Third Party Dependent)
- Monetize Youtube Videos after Upload (Requires access to Content ID API)
- Add Track to Soundcloud (Soundcloud has "temporarily" shutdown new API access for multiple years)
