# omar-scdf-image-info
The Image Info application is a Spring Cloud Data Flow (SCDF) Processor.
This means it:
1. Receives a message on a Spring Cloud input stream using Kafka.
2. Performs an operation on the data.
3. Sends the result on a Spring Cloud output stream using Kafka to a listening SCDF Processor or SCDF Sink.

## Purpose
The Image Info app receives a JSON message from the Stager app containing the filename and path of a staged image, as well as the result of the stage. If the histogram and overview were successfully created, the Image Info app then attempts to retrieve the metadata for the given image. Finally, the Image Info app passes that metadata on to the Indexer app to have it added to the database.

## JSON Input Example (from the Stager)
```json
{
   "filename":"/data/2017/06/22/09/933657b1-6752-42dc-98d8-73ef95a5e780/12345/SCDFTestImages/tiff/14SEP12113301-M1BS-053951940020_01_P001.TIF",
   "stagedSuccessfully":true
}
```

## JSON Output Example (to the Indexer)
```json
{
   "filename":"/data/2017/06/22/09/933657b1-6752-42dc-98d8-73ef95a5e780/12345/SCDFTestImages/tiff/14SEP12113301-M1BS-053951940020_01_P001.TIF",
   "metadataCreated":true
}.
```
