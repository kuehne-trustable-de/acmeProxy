package de.trustable.ca3s.acmeproxy.service.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Generated;
import javax.validation.Valid;
import java.net.URI;
import java.util.Objects;

/**
 * DirectoryResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-03-27T13:33:05.045561200+02:00[Europe/Berlin]")
public class DirectoryResponse {

  @JsonProperty("revokeUri")
  private URI revokeUri;

  @JsonProperty("newNonce")
  private URI newNonce;

  @JsonProperty("newAccount")
  private URI newAccount;

  @JsonProperty("newOrder")
  private URI newOrder;

  @JsonProperty("newAuthz")
  private URI newAuthz;

  @JsonProperty("keyChange")
  private URI keyChange;

  @JsonProperty("revokeCert")
  private URI revokeCert;

  public DirectoryResponse revokeUri(URI revokeUri) {
    this.revokeUri = revokeUri;
    return this;
  }

  /**
   * Get revokeUri
   * @return revokeUri
  */
  @Valid
  @Schema(name = "revokeUri", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public URI getRevokeUri() {
    return revokeUri;
  }

  public void setRevokeUri(URI revokeUri) {
    this.revokeUri = revokeUri;
  }

  public DirectoryResponse newNonce(URI newNonce) {
    this.newNonce = newNonce;
    return this;
  }

  /**
   * Get newNonce
   * @return newNonce
  */
  @Valid
  @Schema(name = "newNonce", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public URI getNewNonce() {
    return newNonce;
  }

  public void setNewNonce(URI newNonce) {
    this.newNonce = newNonce;
  }

  public DirectoryResponse newAccount(URI newAccount) {
    this.newAccount = newAccount;
    return this;
  }

  /**
   * Get newAccount
   * @return newAccount
  */
  @Valid
  @Schema(name = "newAccount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public URI getNewAccount() {
    return newAccount;
  }

  public void setNewAccount(URI newAccount) {
    this.newAccount = newAccount;
  }

  public DirectoryResponse newOrder(URI newOrder) {
    this.newOrder = newOrder;
    return this;
  }

  /**
   * Get newOrder
   * @return newOrder
  */
  @Valid
  @Schema(name = "newOrder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public URI getNewOrder() {
    return newOrder;
  }

  public void setNewOrder(URI newOrder) {
    this.newOrder = newOrder;
  }

  public DirectoryResponse newAuthz(URI newAuthz) {
    this.newAuthz = newAuthz;
    return this;
  }

  /**
   * Get newAuthz
   * @return newAuthz
  */
  @Valid
  @Schema(name = "newAuthz", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public URI getNewAuthz() {
    return newAuthz;
  }

  public void setNewAuthz(URI newAuthz) {
    this.newAuthz = newAuthz;
  }

  public DirectoryResponse keyChange(URI keyChange) {
    this.keyChange = keyChange;
    return this;
  }

  /**
   * Get keyChange
   * @return keyChange
  */
  @Valid
  @Schema(name = "keyChange", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public URI getKeyChange() {
    return keyChange;
  }

  public void setKeyChange(URI keyChange) {
    this.keyChange = keyChange;
  }

  public DirectoryResponse revokeCert(URI revokeCert) {
    this.revokeCert = revokeCert;
    return this;
  }

  /**
   * Get revokeCert
   * @return revokeCert
  */
  @Valid
  @Schema(name = "revokeCert", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  public URI getRevokeCert() {
    return revokeCert;
  }

  public void setRevokeCert(URI revokeCert) {
    this.revokeCert = revokeCert;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DirectoryResponse directoryResponse = (DirectoryResponse) o;
    return Objects.equals(this.revokeUri, directoryResponse.revokeUri) &&
        Objects.equals(this.newNonce, directoryResponse.newNonce) &&
        Objects.equals(this.newAccount, directoryResponse.newAccount) &&
        Objects.equals(this.newOrder, directoryResponse.newOrder) &&
        Objects.equals(this.newAuthz, directoryResponse.newAuthz) &&
        Objects.equals(this.keyChange, directoryResponse.keyChange) &&
        Objects.equals(this.revokeCert, directoryResponse.revokeCert);
  }

  @Override
  public int hashCode() {
    return Objects.hash(revokeUri, newNonce, newAccount, newOrder, newAuthz, keyChange, revokeCert);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DirectoryResponse {\n");
    sb.append("    revokeUri: ").append(toIndentedString(revokeUri)).append("\n");
    sb.append("    newNonce: ").append(toIndentedString(newNonce)).append("\n");
    sb.append("    newAccount: ").append(toIndentedString(newAccount)).append("\n");
    sb.append("    newOrder: ").append(toIndentedString(newOrder)).append("\n");
    sb.append("    newAuthz: ").append(toIndentedString(newAuthz)).append("\n");
    sb.append("    keyChange: ").append(toIndentedString(keyChange)).append("\n");
    sb.append("    revokeCert: ").append(toIndentedString(revokeCert)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

