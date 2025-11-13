import React, { useState, useEffect, useRef } from "react";
import styles from "./App.module.css";
import ReactMarkdown from "react-markdown";
import { v4 as uuidv4 } from "uuid";
import { useQuery } from "@tanstack/react-query";

const App: React.FC = () => {
  const [sessionId, setSessionId] = useState("");
  const [question, setQuestion] = useState("");
  const [messages, setMessages] = useState<{ sender: string; text: string }[]>(
    []
  );
  const currentQuestionRef = useRef("");
  const chatBoxRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    setSessionId(uuidv4());
  }, []);

  const {
    data: botResponse,
    refetch,
    isFetching,
    isError,
  } = useQuery({
    queryKey: ["chatResponse", sessionId],
    queryFn: async () => {
      const q = currentQuestionRef.current;
      const response = await fetch(
        `${
          process.env.REACT_APP_API
        }${sessionId}/assistant?question=${encodeURIComponent(q)}`
      );
      if (!response.ok) throw new Error("Failed to fetch");
      return await response.text();
    },
    enabled: false,
  });

  useEffect(() => {
    if (botResponse) {
      setMessages((prev) => [...prev, { sender: "bot", text: botResponse }]);
    }
  }, [botResponse]);

  const sendMessage = async () => {
    if (!question.trim()) return;

    currentQuestionRef.current = question;
    setMessages((prev) => [...prev, { sender: "user", text: question }]);
    setQuestion(""); // Clear input immediately for UX
    await refetch(); // Trigger bot response
  };

  useEffect(() => {
    if (chatBoxRef.current) {
      chatBoxRef.current.scrollTop = chatBoxRef.current.scrollHeight;
    }
  }, [messages]);

  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <h1>🐾 Pooch Palace Chat</h1>
        <p>Ask me anything about adopting a furry friend!</p>
      </header>
      <div className={styles.chatBox} ref={chatBoxRef}>
        {messages.map((msg, idx) => (
          <div
            key={idx}
            className={msg.sender === "user" ? styles.userMsg : styles.botMsg}
          >
            <ReactMarkdown>{msg.text}</ReactMarkdown>
          </div>
        ))}
        {isFetching && (
          <div className={styles.typingIndicator}>
            <span className={styles.dot}></span>
            <span className={styles.dot}></span>
            <span className={styles.dot}></span>
            <span className={styles.label}>Pooch Palace is typing…</span>
            <span className={styles.paws}>🐾🐾🐾</span>
          </div>
        )}
        {isError && (
          <div className={styles.botMsg}>
            Oops! Something went wrong fetching Pooch Palace's reply.
          </div>
        )}
      </div>
      <div className={styles.inputArea}>
        <input
          type="text"
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              e.preventDefault();
              sendMessage();
            }
          }}
          placeholder="Type your question..."
          autoFocus
          disabled={isFetching}
        />
        <button onClick={sendMessage}>Send</button>
      </div>
    </div>
  );
};

export default App;
