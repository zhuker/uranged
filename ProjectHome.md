**uranged** is a simple http server with Partial Content support (Content-Range header).
  * Targeted at html5 video development.
  * Supports limiting bandwidth to simulate loading over the Internet.
  * Runs from a self-contained jar file.
  * Streams files in your home folder. Nothing else.
  * Jetty based.

## Usage ##
  1. Download jar
  1. `java -jar uranged-0.0.1-uberjar.jar`
  1. point your browser to http://localhost:8085
  1. Stream video from your desktop "http://localhost:8085/Desktop/video.mp4"
  1. Stream video from your desktop in a nice html5 video tag "http://localhost:8085/Desktop/video.mp4?html5video"

## Tested on ##
  1. Chrome 8
  1. Safari 5
  1. WebKit nightlies


---

Development sponsored by [VideoGorillas](http://videogorillas.com/)