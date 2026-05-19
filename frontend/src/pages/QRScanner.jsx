import React, { useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { livreurAPI } from "../services/api";

function QRScanner() {
  const { commandeId } = useParams();
  const navigate = useNavigate();

  useEffect(() => {

    const scan = async () => {

      let input = prompt("Entrer QR (COMMANDE_15)");

      if (!input) {
        navigate("/livreur/dashboard");
        return;
      }

      // CLEAN ULTRA IMPORTANT
      const qr = input.trim().replace(/"/g, "").replace(/\s/g, "");

      console.log("QR envoyé =", qr);

      if (!qr.startsWith("COMMANDE_")) {
        alert("QR invalide");
        return;
      }

      try {
        const response = await livreurAPI.scanQR(qr);

        alert("Livraison confirmée ✅");
        navigate("/livreur/dashboard");

      } catch (err) {
        console.log(err);

        alert(
          "Erreur backend: " +
          (err.response?.data || err.message)
        );
      }
    };

    scan();

  }, [commandeId, navigate]);

  return <h2>Scan QR...</h2>;
}

export default QRScanner;