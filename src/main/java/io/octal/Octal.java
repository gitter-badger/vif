package io.octal;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.octal.proto.File;
import io.octal.proto.ProjectFiles;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class Octal {
    private static final Logger logger = Logger.getLogger(Octal.class.getName());

    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        server = ServerBuilder.forPort(port)
                .addService(new OctalImpl())
                .maxInboundMessageSize(1000000000)
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    Octal.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final Octal server = new Octal();
        server.start();
        server.blockUntilShutdown();
    }

    static class OctalImpl extends io.octal.proto.OctalGrpc.OctalImplBase {

        @Override
        public void sendFile(io.octal.proto.ProjectFiles req, StreamObserver<io.octal.proto.ProjectFiles> responseObserver) {
            System.out.println("Send file function called");
            System.out.println("Recieved " + req.getFilesList().size() + " files");
             List<File> inputFilesList = req.getFilesList();

            //TODO: Get files from request and send to appropriate compiler
            io.octal.proto.ProjectFiles.Builder fb = io.octal.proto.ProjectFiles.newBuilder();
            fb.addAllFiles(new ArrayList<File>());
            fb.setEpoch(Instant.now().toEpochMilli());
            ProjectFiles reply = fb.build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}