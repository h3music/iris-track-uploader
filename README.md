# iris-track-uploader
Program to create youtube video, upload to Youtube, upload files to google drive, and create website product.

## Purpose
1. Reads parameters from csv file
2. Creates Zip file for trackouts
3. Reads Acid Chunk from WAV File
4. Upload files to Google Drive
5. Create Wordpress/Woocommerce product
6. Create video for youtube
7. Upload video to Youtube

## Dependencies
1. FFMPEG
2. com.google.api-client:google-api-client:1.23.0
3. com.google.oauth-client:google-oauth-client-jetty:1.23.0
4. com.google.apis:google-api-services-youtube:v3-rev222-1.25.0
5. com.google.apis:google-api-services-drive:v3-rev20230206-2.0.0
6. https://github.com/calatonsystems/wc-api-java (included in project file)

## Roadmap
- Create User-Friendly Interface
- Multithread Zip file creation
- Provide further error checking and data validation
