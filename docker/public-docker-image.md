# Member 2 - Publish Docker image

**Summary**

- Chuẩn hóa luồng CI để tạo và publish Docker images phục vụ CD. -
- Workflow được cập nhật để mọi branch push đều build và push image với tag theo `github.sha`, đồng thời khi code lên main sẽ push thêm tag `latest` làm bản mặc định cho luồng triển khai.
- Kết quả đạt được là mỗi service đều có image URI rõ ràng theo format docker.io/&lt;dockerhub_user&gt;/yas-&lt;service&gt;:&lt;github.sha&gt; cho branch build và docker.io/&lt;dockerhub_user&gt;/yas-&lt;service&gt;:latest cho main.

`->` Member 3/Jenkins có thể pull đúng image theo commit cụ thể hoặc dùng latest làm fallback cho CD.

**Convention**

- Dùng SHA tag để deploy đúng version
- Dùng latest làm fallback cho main

Format chung:

- SHA tag: docker.io/tnnhuaa/yas-&lt;service&gt;:&lt;commit-sha&gt;
- Main fallback: docker.io/tnnhuaa/yas-&lt;service&gt;:latest

**Danh sách service:**

- docker.io/tnnhuaa/yas-backoffice-bff:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-backoffice-bff:latest
- docker.io/tnnhuaa/yas-backoffice:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-backoffice:latest
- docker.io/tnnhuaa/yas-cart:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-cart:latest
- docker.io/tnnhuaa/yas-customer:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-customer:latest
- docker.io/tnnhuaa/yas-inventory:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-inventory:latest
- docker.io/tnnhuaa/yas-location:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-location:latest
- docker.io/tnnhuaa/yas-media:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-media:latest
- docker.io/tnnhuaa/yas-order:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-order:latest
- docker.io/tnnhuaa/yas-payment:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-payment:latest
- docker.io/tnnhuaa/yas-payment-paypal:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-payment-paypal:latest
- docker.io/tnnhuaa/yas-product:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-product:latest
- docker.io/tnnhuaa/yas-promotion:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-promotion:latest
- docker.io/tnnhuaa/yas-rating:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-rating:latest
- docker.io/tnnhuaa/yas-recommendation:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-recommendation:latest
- docker.io/tnnhuaa/yas-sampledata:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-sampledata:latest
- docker.io/tnnhuaa/yas-search:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-search:latest
- docker.io/tnnhuaa/yas-storefront-bff:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-storefront-bff:latest
- docker.io/tnnhuaa/yas-storefront:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-storefront:latest
- docker.io/tnnhuaa/yas-tax:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-tax:latest
- docker.io/tnnhuaa/yas-webhook:&lt;commit-sha&gt;
- docker.io/tnnhuaa/yas-webhook:latest
