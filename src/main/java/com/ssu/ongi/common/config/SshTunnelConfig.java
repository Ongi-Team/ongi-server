package com.ssu.ongi.common.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile("dev")
public class SshTunnelConfig {

    private Session session;

    public SshTunnelConfig(
            @Value("${ssh.host}") String sshHost,
            @Value("${ssh.port}") int sshPort,
            @Value("${ssh.user}") String sshUser,
            @Value("${ssh.key-path}") String sshKeyPath,
            @Value("${ssh.rds-host}") String rdsHost,
            @Value("${ssh.rds-port}") int rdsPort,
            @Value("${ssh.local-port}") int localPort
    ) {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(sshKeyPath);

            session = jsch.getSession(sshUser, sshHost, sshPort);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            session.setPortForwardingL(localPort, rdsHost, rdsPort);
            log.info("SSH 터널 연결 성공: 127.0.0.1:{} -> {}:{}", localPort, rdsHost, rdsPort);
        } catch (Exception e) {
            log.error("SSH 터널 연결 실패", e);
            throw new RuntimeException("SSH 터널 연결에 실패했습니다.", e);
        }
    }

    @PreDestroy
    public void closeTunnel() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            log.info("SSH 터널 연결 종료");
        }
    }
}
