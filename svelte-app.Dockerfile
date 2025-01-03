FROM node:22-bookworm-slim

RUN apt-get update
RUN apt install -y curl webp

RUN curl -sSf https://atlasgo.sh | sh

RUN npm install -g pnpm

USER node

WORKDIR /home/node
COPY --chown=node:node . .

ENV CI=true

RUN pnpm install
RUN pnpm run --filter @as/svelte-app build

ENV NODE_ENV=production

CMD ["node", "./packages-node/svelte-app/build"]
