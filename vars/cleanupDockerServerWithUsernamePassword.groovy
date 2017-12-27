def call(def credentialsId, def serverName) {
    echo 'Cleanup docker server'
	
	dockerSocket = './' + UUID.randomUUID() + '-docker.sock'
	sshPort = 22
	
	withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: "${credentialsId}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
		// https://medium.com/@dperny/forwarding-the-docker-socket-over-ssh-e6567cfab160
		sh "sshpass -p ${PASSWORD} ssh -f -T -o StrictHostKeyChecking=no -o ExitOnForwardFailure=yes -L '${dockerSocket}':/var/run/docker.sock -p ${sshPort} ${USERNAME}@${serverName}  sleep 10"
	}
	
	withDockerServer ([credentialsId: '', uri: "unix://${dockerSocket}"]) {
		sh "docker system prune -f"
	}
	
	sh "rm -f '${dockerSocket}'"
}
