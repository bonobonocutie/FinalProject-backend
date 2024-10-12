<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>상품스캔</title>
    <script src="https://cdn.jsdelivr.net/npm/jsqr/dist/jsQR.js" charset="utf-8"></script>
    <style>
        #preview {
            width: 100%;
            height: auto;
        }

        #canvas {
            width: 100%;
            height: auto;
            position: absolute;
            top: 0;
            left: 0;
            transform: scaleX(-1);
        }

        #video {
            position: relative;
            width: 100%;
            height: auto;
            transform: scaleX(-1); /* 비디오 좌우 반전 */
        }

        /* 미디어 쿼리 */
        @media (max-width: 600px) {
            .scanner-overlay {
                width: 80px;
                height: 80px;
                margin-top: -40px;
                margin-left: -40px;
            }
        }
    </style>
    <script>
        document.addEventListener('DOMContentLoaded', (event) => {
            const video = document.getElementById('video');
            const canvas = document.getElementById('canvas');
            const context = canvas.getContext('2d');
            let isScanning = false;
            const scanInterval = 2000; // 2초 간격으로 스캔 시도

            function drawLine(begin, end, color) {
                context.beginPath();
                context.moveTo(begin.x, begin.y);
                context.lineTo(end.x, end.y);
                context.lineWidth = 4;
                context.strokeStyle = color;
                context.stroke();
            }

            function scanQRCode() {
                if (video.readyState === video.HAVE_ENOUGH_DATA) {
                    canvas.width = video.videoWidth;
                    canvas.height = video.videoHeight;
                    context.drawImage(video, 0, 0, canvas.width, canvas.height);
                    const imageData = context.getImageData(0, 0, canvas.width, canvas.height);
                    const code = jsQR(imageData.data, imageData.width, imageData.height, {
                        inversionAttempts: "dontInvert",
                    });

                    if (code && !isScanning) {
                        const qrCodeText = code.data.trim();
                        isScanning = true;
                        drawLine(code.location.topLeftCorner, code.location.topRightCorner, "#FF3B58");
                        drawLine(code.location.topRightCorner, code.location.bottomRightCorner, "#FF3B58");
                        drawLine(code.location.bottomRightCorner, code.location.bottomLeftCorner, "#FF3B58");
                        drawLine(code.location.bottomLeftCorner, code.location.topLeftCorner, "#FF3B58");

                        console.log("QR Code found: ", qrCodeText);

                        fetch('http://localhost:8090/petShop/cart/addCart', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json; charset=UTF-8'
                            },
                            body: JSON.stringify({qrCodeText: qrCodeText})
                        })
                            .then(response => {
                                return response.json().then(data => {
                                    if (response.ok) {
                                        alert(data.message);
                                    } else {
                                        alert('Error: ' + data.message);
                                    }
                                });
                            })
                            .catch(error => {
                                alert('Error: ' + error.message);
                            })
                            .finally(() => {
                                // 일정 시간 대기 후 스캔 상태 초기화
                                setTimeout(() => {
                                    isScanning = false;
                                }, scanInterval);
                            });
                    }
                }
                requestAnimationFrame(scanQRCode);
            }

            if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
                navigator.mediaDevices.getUserMedia({video: {facingMode: "environment"}})
                    .then(stream => {
                        video.srcObject = stream;
                        video.setAttribute("playsinline", true); // iOS에서 전체 화면 모드 방지
                        video.play();
                        requestAnimationFrame(scanQRCode);
                    })
                    .catch(err => {
                        console.error('Error accessing camera: ', err);
                        alert('Error accessing camera: ' + err.message);
                    });
            } else {
                alert('Sorry, your browser does not support getUserMedia');
            }
        });
    </script>
</head>
<body>
<div style="position: relative; width: 100%; max-width: 400px; margin: auto;">
    <video id="video" playsinline></video>
    <canvas id="canvas"></canvas>
</div>
</body>
</html>
