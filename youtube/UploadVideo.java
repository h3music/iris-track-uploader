/*
 * Adapted from Google Inc, and Jeremy Walker.
 * (https://github.com/youtube/api-samples/blob/master/java/src/main/java/com/google/api/services/samples/youtube/cmdline/data/UploadVideo.java)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.common.collect.Lists;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Upload a video to the authenticated user's channel. Use OAuth 2.0 to
 * authorize the request. Note that you must add your video files to the
 * project folder to upload them with this application.
 *
 * @author Jeremy Walker
 */
public class UploadVideo {
    public static void upload(YoutubeRecord record) {
        try {
            String MIME_TYPE = "video/*";

            List<String> scopes = Lists.newArrayList(
                    "https://www.googleapis.com/auth/youtube.upload",
                    "https://www.googleapis.com/auth/youtube.force-ssl");

            Credential credential = Auth.authorize(scopes, "uploadvideo");

            YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT,Auth.JSON_FACTORY, credential)
                    .setApplicationName("youtube")
                    .build();

            System.out.println("Uploading Video");

            Video videoObjDefiningMetadata = new Video();

            videoObjDefiningMetadata.setStatus(getVideoStatus(record));
            videoObjDefiningMetadata.setSnippet(getVideoSnippet(record));

            File mediaFile = record.video();

            InputStreamContent mediaContent = new InputStreamContent(MIME_TYPE,
                    new BufferedInputStream(new FileInputStream(mediaFile)));

            YouTube.Videos.Insert videoInsert = youtube.videos()
                    .insert("snippet,status", videoObjDefiningMetadata, mediaContent);

            MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

            uploader.setDirectUploadEnabled(false);

            MediaHttpUploaderProgressListener progressListener = mediaHttpUploader -> {
                switch (mediaHttpUploader.getUploadState()) {
                    case INITIATION_STARTED -> System.out.println("Initiation Started");
                    case INITIATION_COMPLETE -> System.out.println("Initiation Completed");
                    case MEDIA_IN_PROGRESS ->  getProgress(mediaFile, mediaHttpUploader);
                    case MEDIA_COMPLETE -> System.out.println("Upload Completed!");
                    case NOT_STARTED -> System.out.println("Upload Not Started!");
                }
            };

            uploader.setProgressListener(progressListener);

            Video returnedVideo = videoInsert.execute();
            String id = returnedVideo.getId();

            System.out.println("Uploaded: https://youtu.be/" + returnedVideo.getId());

            setThumbnail(id, youtube, record);
            insertPlaylist(id, youtube);

        } catch (TokenResponseException tokenFail) {
            System.out.println("Token Expired");
            File token = new File(System.getProperty("user.home") + "/" + Auth.getCredDirectory() + "/" + "uploadvideo");
            token.delete();
            upload(record);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }


    private static VideoSnippet getVideoSnippet(YoutubeRecord record) {
        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(record.title());
        snippet.setDescription(record.description());
        snippet.setTags(List.of(record.tags().split(",")));
        snippet.setCategoryId("10");
        return snippet;
    }

    private static VideoStatus getVideoStatus(YoutubeRecord record) {
        VideoStatus status = new VideoStatus();
        status.setSelfDeclaredMadeForKids(false);
        status.setPrivacyStatus("private");

        Date date = Date.from((record.publishDateTime()
                .atZone(ZoneId.systemDefault()).toInstant()));
        DateTime dateTime = new DateTime(date);

        status.setPublishAt(dateTime);
        return status;
    }

    private static void getProgress(File mediaFile, MediaHttpUploader mediaHttpUploader) {
        long fileSize = mediaFile.getTotalSpace();
        double percentage = (double) mediaHttpUploader.getNumBytesUploaded() / fileSize;
        String s = String.format("%.2f %%%n", percentage);
//        System.out.println("Upload in progress");
//        System.out.println("Upload percentage: " + s);
    }

    private static void setThumbnail(String id, YouTube youtube, YoutubeRecord record) throws IOException {

        String mimeType = "image/jpeg";

        System.out.println("Uploading Thumbnail");

        InputStreamContent mediaContent = new InputStreamContent(mimeType,
                new BufferedInputStream(new FileInputStream(record.thumbnail())));

        YouTube.Thumbnails.Set thumbnailInsert = youtube.thumbnails().set(id,mediaContent);

        thumbnailInsert.execute();

        System.out.println("Thumbnail Set");
    }

    private static void insertPlaylist(String id, YouTube youtube) throws IOException {

        String playlistId = "";

        System.out.println("Adding to Playlist");

        PlaylistItemSnippet snippet = new PlaylistItemSnippet();

        ResourceId resourceId = new ResourceId();
        resourceId.setKind("youtube#video");
        resourceId.setVideoId(id);

        snippet.setPlaylistId(playlistId);
        snippet.setResourceId(resourceId);

        PlaylistItem playlistItem = new PlaylistItem();
        playlistItem.setSnippet(snippet);

        YouTube.PlaylistItems.Insert playlistInsert = youtube.playlistItems().insert(
                "snippet",playlistItem);

        playlistInsert.execute();

        System.out.println("Add to Playlist");
    }
}