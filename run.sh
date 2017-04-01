# Using an absolute path for some_file is important.  In other words,
#
# docker run --rm -v some_file:/file_in_container docker-test:latest
#
# makes it so /file_in_container is actually an empty directory
docker run --rm -v $PWD/some_file:/file_in_container docker-test:latest
