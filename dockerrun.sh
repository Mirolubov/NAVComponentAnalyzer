docker build . -t nca-core:1.0.0
docker run -it \
  --name nca-core \
  --mount type=bind,source="$(pwd)"/files,target=/core/files \
  nca-core:1.0.0