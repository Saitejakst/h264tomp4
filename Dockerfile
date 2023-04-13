FROM registry.access.redhat.com/ubi8/ubi

RUN dnf install -y https://dl.fedoraproject.org/pub/epel/epel-release-latest-8.noarch.rpm && \
    dnf install -y https://download1.rpmfusion.org/free/el/rpmfusion-free-release-8.noarch.rpm && \
    dnf install -y ffmpeg

CMD ["/bin/bash"]
