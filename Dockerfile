FROM ubuntu:latest
LABEL authors="David Souza"

ENTRYPOINT ["top", "-b"]